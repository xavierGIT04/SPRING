package tg.nassara.estimation.service.marche;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.service.parametrage.QuartierService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Enregistre les annonces lues sur le catalogue (transaction courte, après la lecture HTTP).
 * Étape 5 : seules les VENTES avec un prix sont retenues ; les locations viendront avec la méthode 3
 * (périodicité et meublé à gérer).
 */
@Service
public class PersisteurAnnoncesCatalogue {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");

    private final AnnonceMarcheRepository annonceRepository;
    private final QuartierRepository quartierRepository;

    public PersisteurAnnoncesCatalogue(AnnonceMarcheRepository annonceRepository,
                                       QuartierRepository quartierRepository) {
        this.annonceRepository = annonceRepository;
        this.quartierRepository = quartierRepository;
    }

    public record Bilan(int retenues, int desactivees) {
    }

    private record CleQuartier(String cle, Quartier quartier) {
    }

    @Transactional
    public Bilan appliquer(String source, List<AnnonceCollectee> collectees) {
        List<CleQuartier> cles = construireCles();
        LocalDate aujourdhui = LocalDate.now(FUSEAU);
        Set<String> urlsRetenues = new HashSet<>();
        int retenues = 0;

        for (AnnonceCollectee c : collectees) {
            if (c.typeAnnonce() != TypeAnnonce.VENTE || c.nature() == null || c.montant() == null) {
                continue;
            }
            AnnonceMarche annonce = annonceRepository.findByUrlSource(c.urlSource())
                    .orElseGet(AnnonceMarche::new);
            if (annonce.getId() != null && annonce.getOrigine() == OrigineAnnonce.SAISIE_AGENT) {
                continue; // jamais d'écrasement d'une saisie d'agent
            }
            annonce.setSource(source);
            annonce.setOrigine(OrigineAnnonce.CATALOGUE_AGENCE);
            annonce.setUrlSource(c.urlSource());
            annonce.setTitre(c.titre());
            annonce.setTypeAnnonce(c.typeAnnonce());
            annonce.setNature(c.nature());
            annonce.setMontant(c.montant());
            // Hypothèse à valider avec l'agence : la surface affichée au catalogue est celle du terrain.
            annonce.setSurfaceTerrainM2(c.surfaceM2());
            annonce.setQuartierBrut(c.quartierBrut());
            annonce.setQuartier(rapprocher(c.titre(), cles));
            annonce.setDateCollecte(aujourdhui);
            annonce.setActif(true);
            annonceRepository.save(annonce);
            urlsRetenues.add(c.urlSource());
            retenues++;
        }

        int desactivees = 0;
        for (AnnonceMarche existante : annonceRepository.findByOrigineAndActifTrue(OrigineAnnonce.CATALOGUE_AGENCE)) {
            if (!urlsRetenues.contains(existante.getUrlSource())) {
                existante.setActif(false); // retirée du catalogue, ou sans prix désormais
                desactivees++;
            }
        }
        return new Bilan(retenues, desactivees);
    }

    /** Clés de rapprochement (code et libellé normalisés), les plus longues d'abord : « TOKOIN-OUEST » avant « TOKOIN ». */
    private List<CleQuartier> construireCles() {
        List<CleQuartier> cles = new ArrayList<>();
        for (Quartier quartier : quartierRepository.findByActifTrueOrderByLibelleAsc()) {
            Set<String> variantes = new LinkedHashSet<>();
            variantes.add(QuartierService.normaliser(quartier.getCode()));
            variantes.add(QuartierService.normaliser(quartier.getLibelle()));
            for (String variante : variantes) {
                if (!variante.isEmpty()) {
                    cles.add(new CleQuartier(variante, quartier));
                }
            }
        }
        cles.sort(Comparator.comparingInt((CleQuartier c) -> c.cle().length()).reversed());
        return cles;
    }

    private static Quartier rapprocher(String titre, List<CleQuartier> cles) {
        String cible = "-" + QuartierService.normaliser(titre) + "-";
        for (CleQuartier c : cles) {
            if (cible.contains("-" + c.cle() + "-")) {
                return c.quartier();
            }
        }
        return null;
    }
}
