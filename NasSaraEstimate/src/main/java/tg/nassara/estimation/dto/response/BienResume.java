package tg.nassara.estimation.dto.response;

import org.hibernate.Hibernate;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;
import java.time.Instant;

/** Ligne de la liste des biens. À construire dans une transaction ouverte (quartier chargé à la demande). */
public record BienResume(Long id, NatureBien nature, ReferenceResume quartier, String adresse,
                         BigDecimal surfaceTerrainM2, Instant dateCreation) {

    public static BienResume depuis(Bien bien) {
        Bien reel = (Bien) Hibernate.unproxy(bien);
        return new BienResume(reel.getId(), reel.nature(), ReferenceResume.de(reel.getQuartier()), reel.getAdresse(),
                reel.getSurfaceTerrainM2(), reel.getDateCreation());
    }
}
