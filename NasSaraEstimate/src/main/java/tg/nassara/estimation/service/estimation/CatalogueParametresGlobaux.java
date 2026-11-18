package tg.nassara.estimation.service.estimation;

import tg.nassara.estimation.enums.NiveauParametre;

import java.util.List;

import static tg.nassara.estimation.enums.NiveauParametre.AVANCE;
import static tg.nassara.estimation.enums.NiveauParametre.COURANT;

/**
 * Catalogue des paramètres globaux : valeur initiale, unité, niveau et description lisible par un agent.
 * Les valeurs sont des valeurs de DÉPART (test), à faire valider par l'agence. Elles sont copiées en base
 * au démarrage si le paramètre n'existe pas ; ensuite, seule la base fait foi.
 */
public final class CatalogueParametresGlobaux {

    private CatalogueParametresGlobaux() {
    }

    public record Entree(String cle, String valeur, String unite, NiveauParametre niveau, String description) {
    }

    public static final List<Entree> ENTREES = List.of(
            // ---------- Réglages courants ----------
            new Entree(ClesParametres.MARCHE_FENETRE_JOURS, "180", "jours", COURANT,
                    "Âge maximal des annonces prises en compte. Au-delà, une annonce est jugée trop ancienne. Ex. : 180 = six mois."),
            new Entree(ClesParametres.MARCHE_MIN_COMPARABLES, "2", "nombre", COURANT,
                    "Nombre minimal d'annonces comparables pour que la comparaison de marché donne un résultat. En dessous, cette méthode est écartée."),
            new Entree(ClesParametres.MARCHE_MAX_COMPARABLES, "10", "nombre", COURANT,
                    "Nombre maximal d'annonces retenues (les plus proches en surface, puis les plus récentes)."),
            new Entree(ClesParametres.MARCHE_TOLERANCE_SURFACE, "0.30", "fraction", COURANT,
                    "Écart de surface accepté entre une annonce et le bien. Ex. : 0,30 = on compare avec des terrains de 30 % plus petits à 30 % plus grands."),
            new Entree(ClesParametres.MARCHE_SURFACE_LOT_M2, "600", "m²", COURANT,
                    "Surface d'un lot, utilisée quand une annonce indique un nombre de lots au lieu d'une surface."),
            new Entree(ClesParametres.MARCHE_DECOTE_NEGOCIATION_DEFAUT, "0.90", "facteur", COURANT,
                    "Tant que l'agence n'a pas assez de ventes validées, on suppose qu'un bien se vend à ce niveau du prix affiché. Ex. : 0,90 = 10 % moins cher que l'annonce."),
            new Entree(ClesParametres.MARCHE_MIN_TRANSACTIONS_CORRECTION, "5", "nombre", COURANT,
                    "Nombre de ventes validées dans le quartier à partir duquel l'outil mesure lui-même l'écart entre prix affiché et prix de vente."),
            new Entree(ClesParametres.HEDONIQUE_MARGE_FOURCHETTE, "0.10", "fraction", COURANT,
                    "Largeur de la fourchette autour du prix de la méthode 1. Ex. : 0,10 = de 10 % en dessous à 10 % au-dessus."),
            new Entree(ClesParametres.CAPITALISATION_TAUX_DEFAUT, "0.08", "fraction", COURANT,
                    "Rendement annuel attendu (loyer annuel ÷ prix) quand le quartier n'a pas le sien. Ex. : 0,08 = 8 %."),
            new Entree(ClesParametres.CAPITALISATION_TAUX_CHARGES, "0.15", "fraction", COURANT,
                    "Part du loyer perdue en vacance locative, charges et impôts. Ex. : 0,15 = 15 % du loyer n'est pas encaissé net."),
            new Entree(ClesParametres.CAPITALISATION_MIN_COMPARABLES, "2", "nombre", COURANT,
                    "Nombre minimal de loyers comparables pour s'appuyer sur le marché. En dessous, le loyer actuel du bien est utilisé s'il est renseigné."),
            new Entree(ClesParametres.CAPITALISATION_NUITEES_PAR_MOIS, "10", "nuits", COURANT,
                    "Nuits payées par mois pour convertir un tarif à la nuitée en loyer mensuel. 0 = les annonces à la nuitée sont ignorées."),
            new Entree(ClesParametres.CAPITALISATION_FACTEUR_MEUBLE, "0.75", "facteur", COURANT,
                    "Un loyer meublé est ramené à un équivalent non meublé en le multipliant par ce nombre. Ex. : 0,75 = le meublé coûte un tiers de plus."),

            // ---------- Réglages avancés (valeurs par défaut, à ne changer qu'en connaissance de cause) ----------
            new Entree(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MIN, "0.40", "facteur", AVANCE,
                    "Avancé. Plancher du cumul des coefficients du site (voie, topographie, statut, viabilisation) : évite qu'une accumulation de défauts fasse trop chuter le prix."),
            new Entree(ClesParametres.HEDONIQUE_CUMUL_COEFFICIENTS_MAX, "1.80", "facteur", AVANCE,
                    "Avancé. Plafond du cumul des coefficients du site : évite qu'une accumulation d'atouts fasse trop monter le prix."),
            new Entree(ClesParametres.HEDONIQUE_CONFIANCE_MIN, "0.30", "fraction", AVANCE,
                    "Avancé. Niveau de confiance de la méthode 1 pour un quartier jamais calibré sur des ventes réelles."),
            new Entree(ClesParametres.HEDONIQUE_CONFIANCE_MAX, "0.85", "fraction", AVANCE,
                    "Avancé. Niveau de confiance de la méthode 1 pour un quartier bien calibré."),
            new Entree(ClesParametres.HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE, "20", "nombre", AVANCE,
                    "Avancé. Nombre de ventes validées dans un quartier pour que la méthode 1 atteigne son niveau de confiance maximal."),
            new Entree(ClesParametres.HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE, "0.60", "facteur", AVANCE,
                    "Avancé. Réduction de confiance quand le prix de base du quartier est provisoire (saisi à chaud, non confirmé)."),
            new Entree(ClesParametres.MARCHE_CORRECTION_MIN, "0.70", "facteur", AVANCE,
                    "Avancé. Plancher du facteur prix de vente ÷ prix affiché mesuré sur les ventes réelles. Ex. : 0,70 = on n'accepte pas plus de 30 % de baisse."),
            new Entree(ClesParametres.MARCHE_CORRECTION_MAX, "1.10", "facteur", AVANCE,
                    "Avancé. Plafond du même facteur. Ex. : 1,10 = on n'accepte pas plus de 10 % de hausse par rapport au prix affiché."),
            new Entree(ClesParametres.MARCHE_CONFIANCE_MIN, "0.20", "fraction", AVANCE,
                    "Avancé. Confiance minimale de la comparaison de marché (peu d'annonces)."),
            new Entree(ClesParametres.MARCHE_CONFIANCE_MAX, "0.80", "fraction", AVANCE,
                    "Avancé. Confiance maximale de la comparaison de marché (beaucoup d'annonces homogènes)."),
            new Entree(ClesParametres.MARCHE_PENALITE_DISPERSION, "1.0", "facteur", AVANCE,
                    "Avancé. Poids de la dispersion des prix dans la confiance : plus les annonces sont hétérogènes, plus la confiance baisse. 0 = ignorée."),
            new Entree(ClesParametres.MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT, "0.70", "facteur", AVANCE,
                    "Avancé. Réduction de confiance quand la décote par défaut est utilisée faute de ventes validées."),
            new Entree(ClesParametres.CAPITALISATION_MARGE_FOURCHETTE, "0.15", "fraction", AVANCE,
                    "Avancé. Marge autour du loyer actuel du bien quand il sert de base faute de loyers de marché."),
            new Entree(ClesParametres.CAPITALISATION_ECART_ALERTE_LOYER_BIEN, "0.25", "fraction", AVANCE,
                    "Avancé. Écart entre le loyer du bien et le loyer de marché au-delà duquel un avertissement est affiché."),
            new Entree(ClesParametres.CAPITALISATION_CONFIANCE_MIN, "0.20", "fraction", AVANCE,
                    "Avancé. Confiance minimale de la capitalisation."),
            new Entree(ClesParametres.CAPITALISATION_CONFIANCE_MAX, "0.80", "fraction", AVANCE,
                    "Avancé. Confiance maximale de la capitalisation."),
            new Entree(ClesParametres.CAPITALISATION_PENALITE_DISPERSION, "1.0", "facteur", AVANCE,
                    "Avancé. Poids de la dispersion des loyers dans la confiance. 0 = ignorée."),
            new Entree(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_TAUX_DEFAUT, "0.80", "facteur", AVANCE,
                    "Avancé. Réduction de confiance quand le taux de capitalisation est celui par défaut et non celui du quartier."),
            new Entree(ClesParametres.CAPITALISATION_CONFIANCE_FACTEUR_LOYER_BIEN, "0.60", "facteur", AVANCE,
                    "Avancé. Réduction de confiance quand la base est le loyer du bien et non le marché."),
            new Entree(ClesParametres.FUSION_ECART_ALERTE_METHODES, "0.30", "fraction", AVANCE,
                    "Avancé. Écart entre les prix des méthodes au-delà duquel un avertissement est affiché. Ex. : 0,30 = 30 %."),
            new Entree(ClesParametres.FUSION_CONFIANCE_FACTEUR_METHODE_UNIQUE, "0.80", "facteur", AVANCE,
                    "Avancé. Réduction de confiance quand une seule méthode a pu conclure (aucun recoupement)."),
            new Entree(ClesParametres.APPRENTISSAGE_LISSAGE, "0.20", "fraction", AVANCE,
                    "Avancé. Part de l'écart constaté (prix de vente réel contre estimation) corrigée à chaque recalibrage du coefficient de zone. Ex. : 0,20 = 20 % de l'écart. 1 = tout l'écart."),
            new Entree(ClesParametres.APPRENTISSAGE_MIN_TRANSACTIONS_QUARTIER, "3", "nombre", AVANCE,
                    "Avancé. Nombre minimal de ventes validées d'un quartier pour modifier son coefficient de zone. En dessous, elles sont conservées pour un prochain recalibrage."),
            new Entree(ClesParametres.APPRENTISSAGE_VARIATION_MAX_CYCLE, "0.10", "fraction", AVANCE,
                    "Avancé. Variation maximale du coefficient de zone d'un quartier à chaque recalibrage. Ex. : 0,10 = au plus 10 % de hausse ou de baisse."),
            new Entree(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MIN, "0.50", "facteur", AVANCE,
                    "Avancé. Valeur plancher du coefficient de zone d'un quartier après recalibrage."),
            new Entree(ClesParametres.APPRENTISSAGE_COEFFICIENT_ZONE_MAX, "2.00", "facteur", AVANCE,
                    "Avancé. Valeur plafond du coefficient de zone d'un quartier après recalibrage."),
            new Entree(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MIN, "0.03", "fraction", AVANCE,
                    "Avancé. Taux de capitalisation plancher d'un quartier après recalibrage. Ex. : 0,03 = 3 %."),
            new Entree(ClesParametres.APPRENTISSAGE_TAUX_CAPITALISATION_MAX, "0.20", "fraction", AVANCE,
                    "Avancé. Taux de capitalisation plafond d'un quartier après recalibrage. Ex. : 0,20 = 20 %.")
    );
}