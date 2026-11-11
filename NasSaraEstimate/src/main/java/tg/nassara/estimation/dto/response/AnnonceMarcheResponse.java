package tg.nassara.estimation.dto.response;

import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.PeriodiciteLoyer;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record AnnonceMarcheResponse(
        Long id,
        Long version,
        OrigineAnnonce origine,
        String source,
        String urlSource,
        String titre,
        TypeAnnonce typeAnnonce,
        PeriodiciteLoyer periodicite,
        Boolean meuble,
        NatureBien nature,
        Long quartierId,
        String quartier,
        String quartierBrut,
        BigDecimal surfaceTerrainM2,
        BigDecimal surfaceBatieM2,
        BigDecimal montant,
        BigDecimal prixAuM2,
        LocalDate dateCollecte,
        boolean actif,
        String saisiePar) {

    /** À appeler dans une transaction ouverte (le quartier est chargé à la demande). Prix au m² : ventes seulement. */
    public static AnnonceMarcheResponse depuis(AnnonceMarche a) {
        BigDecimal prixAuM2 = (a.getTypeAnnonce() == TypeAnnonce.VENTE
                && a.getSurfaceTerrainM2() != null && a.getSurfaceTerrainM2().signum() > 0)
                ? a.getMontant().divide(a.getSurfaceTerrainM2(), 2, RoundingMode.HALF_UP)
                : null;
        return new AnnonceMarcheResponse(a.getId(), a.getVersion(), a.getOrigine(), a.getSource(), a.getUrlSource(),
                a.getTitre(), a.getTypeAnnonce(), a.getPeriodicite(), a.getMeuble(), a.getNature(),
                a.getQuartier() != null ? a.getQuartier().getId() : null,
                a.getQuartier() != null ? a.getQuartier().getLibelle() : null,
                a.getQuartierBrut(), a.getSurfaceTerrainM2(), a.getSurfaceBatieM2(), a.getMontant(), prixAuM2,
                a.getDateCollecte(), a.isActif(), a.getSaisiePar());
    }
}