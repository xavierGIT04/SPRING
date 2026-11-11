package tg.nassara.estimation.service.marche;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.service.parametrage.QuartierService;
import tg.nassara.estimation.enums.PeriodiciteLoyer;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Lit la page « Notre catalogue » et en extrait les annonces.
 *
 * <p>Le découpage ne dépend pas des classes CSS du thème : une annonce = le plus grand bloc de la page
 * qui ne contient qu'un seul lien /Propriété/... Dans ce bloc : prix = plus petit élément contenant « F CFA »,
 * surface = élément dont tout le texte est « 600 m² », type = mots ACHETER / LOUER, nature = étiquettes /status/.
 * À vérifier sur le HTML réel (voir le test).
 */
public final class AnalyseurCatalogue {

    private static final Pattern CHEMIN_ANNONCE = Pattern.compile("^/propri[eé]t[eé]/[^/]+/?$");
    private static final Pattern MONTANT = Pattern.compile(
            "(\\d{1,3}(?:[.,\\s\\u00a0\\u202f]\\d{3})+|\\d+)\\s*F\\s*CFA", Pattern.CASE_INSENSITIVE);
    private static final Pattern SURFACE = Pattern.compile(
            "^(\\d{1,3}(?:[\\s\\u00a0\\u202f]\\d{3})+|\\d+)\\s*m\\s*[²2]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACHETER = Pattern.compile("\\bacheter\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LOUER = Pattern.compile("\\blouer\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SEPARATEUR_TITRE = Pattern.compile("\\s*[–—,]\\s*|\\s+-\\s+|\\s+à\\s+");

    private static final Set<String> NATURES_BATI = Set.of("MAISON", "VILLA", "APPARTEMENT", "IMMEUBLE", "PROPRIETE");
    private static final Pattern NUITEE = Pattern.compile("\\bnuit", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern MOIS = Pattern.compile("\\b(mois|mensuel)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern NON_MEUBLE = Pattern.compile("\\bnon[\\s-]*meubl", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern MEUBLE = Pattern.compile("\\bmeubl", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private AnalyseurCatalogue() {
    }

    public static List<AnnonceCollectee> analyser(String html, String baseUri) {
        Document document = Jsoup.parse(html, baseUri);
        Map<String, Element> premiersLiens = new LinkedHashMap<>();
        for (Element lien : document.select("a[href]")) {
            String url = lien.absUrl("href");
            if (estLienAnnonce(url)) {
                premiersLiens.putIfAbsent(url, lien);
            }
        }
        List<AnnonceCollectee> resultat = new ArrayList<>();
        for (Map.Entry<String, Element> entree : premiersLiens.entrySet()) {
            analyserAnnonce(entree.getKey(), entree.getValue()).ifPresent(resultat::add);
        }
        return resultat;
    }

    private static Optional<AnnonceCollectee> analyserAnnonce(String url, Element lien) {
        Element conteneur = conteneurDe(lien);

        String titre = conteneur.select("a[href]").stream()
                .filter(a -> url.equals(a.absUrl("href")))
                .map(a -> a.text().strip())
                .filter(t -> !t.isEmpty())
                .findFirst()
                .orElse(null);
        if (titre == null) {
            return Optional.empty();
        }

        // Le titre contient souvent « à louer » ou « à vendre » : on le retire avant de chercher ACHETER / LOUER.
        String texteSansTitre = conteneur.text().replace(titre, " ");
        boolean acheter = ACHETER.matcher(texteSansTitre).find();
        boolean louer = LOUER.matcher(texteSansTitre).find();
        TypeAnnonce type = (acheter && !louer) ? TypeAnnonce.VENTE : (louer && !acheter ? TypeAnnonce.LOCATION : null);

        Set<String> categories = conteneur.select("a[href*=\"/status/\"]").stream()
                .map(a -> QuartierService.normaliser(a.text()))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String textePrix = conteneur.getAllElements().stream()
                .map(Element::text)
                .filter(t -> MONTANT.matcher(t).find())
                .min(Comparator.comparingInt(String::length))
                .orElse(null);
        BigDecimal montant = extraireMontant(textePrix);

        BigDecimal surface = conteneur.getAllElements().stream()
                .map(e -> extraireSurface(e.text()))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // Le texte du bloc, titre compris : « meublée » ou « à la nuitée » figurent souvent dans le titre.
        String texteBloc = conteneur.text();
        PeriodiciteLoyer periodicite = type == TypeAnnonce.LOCATION ? determinerPeriodicite(textePrix, texteBloc) : null;
        Boolean meuble = type == TypeAnnonce.LOCATION ? determinerMeuble(texteBloc) : null;

        return Optional.of(new AnnonceCollectee(
                tronquer(url, 600), tronquer(titre, 255), type, determinerNature(categories),
                montant, surface, tronquer(extraireQuartierBrut(titre), 150), periodicite, meuble));
    }

    /**
     * Périodicité d'un loyer d'après le suffixe du prix : « nuit » = nuitée ; « mois » ou « mensuel » = mensuel ;
     * aucun suffixe = mensuel, sauf si le bloc de l'annonce parle de nuitée ; autre suffixe (semaine, etc.) = inconnue (null).
     * Le doute penche toujours vers « nuitée » : un loyer écarté coûte moins qu'une nuitée prise pour un mois.
     */
    static PeriodiciteLoyer determinerPeriodicite(String textePrix, String texteBloc) {
        if (textePrix == null) {
            return null;
        }
        Matcher m = MONTANT.matcher(textePrix);
        if (!m.find()) {
            return null;
        }
        String suffixe = textePrix.substring(m.end()).strip();
        if (NUITEE.matcher(suffixe).find()) {
            return PeriodiciteLoyer.NUITEE;
        }
        if (MOIS.matcher(suffixe).find()) {
            return PeriodiciteLoyer.MENSUEL;
        }
        if (!suffixe.isEmpty()) {
            return null;
        }
        return (texteBloc != null && NUITEE.matcher(texteBloc).find())
                ? PeriodiciteLoyer.NUITEE : PeriodiciteLoyer.MENSUEL;
    }

    /** « non meublé » = faux, « meublé » = vrai, sinon inconnu (null). */
    static Boolean determinerMeuble(String texte) {
        if (texte == null) {
            return null;
        }
        if (NON_MEUBLE.matcher(texte).find()) {
            return Boolean.FALSE;
        }
        if (MEUBLE.matcher(texte).find()) {
            return Boolean.TRUE;
        }
        return null;
    }
    // ---------- Découpage de la page ----------

    static boolean estLienAnnonce(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            String chemin = URI.create(url).getPath();
            return chemin != null && CHEMIN_ANNONCE.matcher(chemin.toLowerCase(Locale.ROOT)).matches();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static Element conteneurDe(Element lien) {
        Element courant = lien;
        while (courant.parent() != null) {
            Element parent = courant.parent();
            if ("body".equals(parent.tagName()) || "html".equals(parent.tagName()) || compterAnnonces(parent) > 1) {
                break;
            }
            courant = parent;
        }
        return courant;
    }

    private static long compterAnnonces(Element element) {
        return element.select("a[href]").stream()
                .map(a -> a.absUrl("href"))
                .filter(AnalyseurCatalogue::estLienAnnonce)
                .distinct()
                .count();
    }

    // ---------- Extraction des champs (visibles pour les tests) ----------

    static BigDecimal extraireMontant(String texte) {
        if (texte == null) {
            return null;
        }
        Matcher m = MONTANT.matcher(texte);
        if (!m.find()) {
            return null;
        }
        String chiffres = m.group(1).replaceAll("\\D", "");
        return chiffres.isEmpty() ? null : new BigDecimal(chiffres);
    }

    static BigDecimal extraireSurface(String texte) {
        if (texte == null) {
            return null;
        }
        Matcher m = SURFACE.matcher(texte.strip());
        if (!m.matches()) {
            return null;
        }
        BigDecimal surface = new BigDecimal(m.group(1).replaceAll("\\D", ""));
        return surface.signum() > 0 ? surface : null;
    }

    /**
     * Nature d'après les étiquettes de l'annonce (dépend de la façon dont l'agence les renseigne).
     * Retourne null (annonce ignorée) pour une villa inachevée, un fonds de commerce ou une étiquette inconnue.
     */
    static NatureBien determinerNature(Set<String> categories) {
        if (categories.contains("VILLA-INACHEVEE") || categories.contains("FONDS-DE-COMMERCE")) {
            return null;
        }
        if (categories.contains("TERRAIN-BATI")) {
            return NatureBien.BATI;
        }
        if (categories.contains("TERRAIN-NU") || categories.contains("TERRAIN-A-BATIR")) {
            return NatureBien.TERRAIN;
        }
        if (!Collections.disjoint(categories, NATURES_BATI)) {
            return NatureBien.BATI;
        }
        return null;
    }

    /** Dernier segment du titre, à titre indicatif (le rapprochement réel se fait sur le titre entier). */
    static String extraireQuartierBrut(String titre) {
        String[] morceaux = SEPARATEUR_TITRE.split(titre.strip());
        for (int i = morceaux.length - 1; i >= 0; i--) {
            if (!morceaux[i].isBlank()) {
                return morceaux[i].strip();
            }
        }
        return titre.strip();
    }

    private static String tronquer(String texte, int max) {
        return texte.length() > max ? texte.substring(0, max) : texte;
    }
}
