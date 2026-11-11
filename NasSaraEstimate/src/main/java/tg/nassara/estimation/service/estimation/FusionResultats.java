package tg.nassara.estimation.service.estimation;

import org.springframework.stereotype.Component;
import tg.nassara.estimation.enums.MethodeEstimation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fusion des résultats des méthodes en une fourchette finale.
 *
 * <p>Poids effectif d'une méthode = poids paramétré × indice de confiance de la méthode, puis normalisé (somme = 1) :
 * une méthode peu fiable pèse moins. Prix min, central et max = moyennes pondérées des bornes correspondantes
 * (combinaison convexe : min <= central <= max est conservé). Confiance globale = moyenne pondérée des confiances.
 *
 * <p>Garde-fous : un écart important entre les prix centraux des méthodes déclenche un avertissement ; une méthode
 * unique (aucun recoupement) réduit la confiance. Les deux seuils sont des paramètres globaux.
 */
@Component
public class FusionResultats {

    private static final int ECHELLE_PART = 10;

    private final ParametresCalcul parametres;

    public FusionResultats(ParametresCalcul parametres) {
        this.parametres = parametres;
    }

    /** Résultat d'une méthode avec le poids paramétré (strictement positif) en vigueur. */
    public record Contribution(MethodeEstimation methode, BigDecimal poidsParametre, ResultatMethodeCalcule resultat) {
        public Contribution {
            Objects.requireNonNull(methode, "La méthode est obligatoire.");
            Objects.requireNonNull(poidsParametre, "Le poids est obligatoire.");
            Objects.requireNonNull(resultat, "Le résultat est obligatoire.");
            if (poidsParametre.signum() <= 0) {
                throw new IllegalArgumentException("Le poids d'une méthode fusionnée doit être strictement positif.");
            }
        }
    }

    /** Part (0 à 1) effectivement appliquée à une méthode dans la fusion. */
    public record Part(MethodeEstimation methode, BigDecimal poidsParametre, BigDecimal partFusion) {
    }

    /** Parts dans le même ordre que les contributions fournies. */
    public record Resultat(BigDecimal prixMin, BigDecimal prixCentral, BigDecimal prixMax,
                           BigDecimal indiceConfiance, List<Part> parts, List<String> avertissements) {
    }

    public Resultat fusionner(List<Contribution> contributions) {
        if (contributions == null || contributions.isEmpty()) {
            throw new IllegalArgumentException("Aucune méthode à fusionner.");
        }
        List<String> avertissements = new ArrayList<>();

        List<BigDecimal> poids = new ArrayList<>();
        for (Contribution c : contributions) {
            poids.add(c.poidsParametre().multiply(c.resultat().indiceConfiance()));
        }
        BigDecimal total = somme(poids);
        if (total.signum() == 0) {
            poids = new ArrayList<>();
            for (Contribution c : contributions) {
                poids.add(c.poidsParametre());
            }
            total = somme(poids);
            avertissements.add("Toutes les méthodes ont un indice de confiance nul : "
                    + "fusion selon les seules pondérations paramétrées.");
        }

        BigDecimal min = BigDecimal.ZERO;
        BigDecimal central = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        BigDecimal confiance = BigDecimal.ZERO;
        List<Part> parts = new ArrayList<>();
        for (int i = 0; i < contributions.size(); i++) {
            Contribution c = contributions.get(i);
            ResultatMethodeCalcule r = c.resultat();
            BigDecimal part = poids.get(i).divide(total, ECHELLE_PART, RoundingMode.HALF_UP);
            min = min.add(part.multiply(r.prixMin()));
            central = central.add(part.multiply(r.prixCentral()));
            max = max.add(part.multiply(r.prixMax()));
            confiance = confiance.add(part.multiply(r.indiceConfiance()));
            parts.add(new Part(c.methode(), c.poidsParametre(), part.setScale(4, RoundingMode.HALF_UP)));
        }

        BigDecimal prixCentral = ResultatMethodeCalcule.arrondiXof(central);
        if (contributions.size() == 1) {
            BigDecimal facteur = fraction(ClesParametres.FUSION_CONFIANCE_FACTEUR_METHODE_UNIQUE);
            confiance = confiance.multiply(facteur);
            avertissements.add("Une seule méthode a conclu (" + contributions.get(0).methode()
                    + ") : aucun recoupement possible, confiance réduite (× "
                    + facteur.stripTrailingZeros().toPlainString() + ").");
        } else {
            signalerEcart(contributions, prixCentral, avertissements);
        }

        return new Resultat(
                ResultatMethodeCalcule.arrondiXof(min),
                prixCentral,
                ResultatMethodeCalcule.arrondiXof(max),
                confiance.max(BigDecimal.ZERO).min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP),
                List.copyOf(parts),
                List.copyOf(avertissements));
    }

    private void signalerEcart(List<Contribution> contributions, BigDecimal centralFusionne,
                               List<String> avertissements) {
        BigDecimal seuil = fraction(ClesParametres.FUSION_ECART_ALERTE_METHODES);
        if (centralFusionne.signum() <= 0) {
            return;
        }
        BigDecimal plusBas = contributions.stream().map(c -> c.resultat().prixCentral())
                .min(Comparator.naturalOrder()).orElseThrow();
        BigDecimal plusHaut = contributions.stream().map(c -> c.resultat().prixCentral())
                .max(Comparator.naturalOrder()).orElseThrow();
        BigDecimal ecart = plusHaut.subtract(plusBas).divide(centralFusionne, 4, RoundingMode.HALF_UP);
        if (ecart.compareTo(seuil) > 0) {
            String detail = contributions.stream()
                    .map(c -> c.methode() + " : " + xof(c.resultat().prixCentral()))
                    .collect(Collectors.joining(" ; "));
            avertissements.add("Écart de " + pourcentage(ecart) + " entre les prix centraux des méthodes (" + detail
                    + ") : à examiner avant de s'appuyer sur la fourchette.");
        }
    }

    private static BigDecimal somme(List<BigDecimal> valeurs) {
        return valeurs.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal fraction(String cle) {
        BigDecimal valeur = parametres.valeur(cle);
        if (valeur.signum() < 0 || valeur.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Paramétrage incohérent : " + cle
                    + " doit être compris entre 0 et 1 (" + valeur + ").");
        }
        return valeur;
    }

    private static String pourcentage(BigDecimal fraction) {
        return fraction.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) + " %";
    }

    private static String xof(BigDecimal montant) {
        return ResultatMethodeCalcule.arrondiXof(montant).toPlainString() + " XOF";
    }
}
