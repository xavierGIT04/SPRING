package tg.nassara.estimation.service.marche;

import java.util.List;

/** Contrat d'une source d'annonces (une classe par source) : catalogue de l'agence aujourd'hui, d'autres plus tard. */
public interface SourceAnnonces {

    /** Nom stocké dans AnnonceMarche.source. */
    String nom();

    /** Lit les annonces de la source. Lève IllegalStateException si la source est injoignable. */
    List<AnnonceCollectee> collecter();
}
