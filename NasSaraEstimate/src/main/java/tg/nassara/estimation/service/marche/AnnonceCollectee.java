package tg.nassara.estimation.service.marche;

import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.math.BigDecimal;

/** Annonce lue sur une source, avant rapprochement du quartier et enregistrement. Les champs inconnus sont nuls. */
public record AnnonceCollectee(
        String urlSource,
        String titre,
        TypeAnnonce typeAnnonce,
        NatureBien nature,
        BigDecimal montant,
        BigDecimal surfaceM2,
        String quartierBrut) {
}
