package tg.nassara.estimation.dto.response;

import org.hibernate.Hibernate;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.NatureBien;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/** Les champs du bâti sont nuls pour un terrain nu. À construire dans une transaction ouverte (relations LAZY). */
public record BienResponse(
        Long id,
        Long version,
        NatureBien nature,
        ReferenceResume quartier,
        String adresse,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal surfaceTerrainM2,
        ReferenceResume typeVoie,
        ReferenceResume topographie,
        ReferenceResume statutJuridique,
        List<ReferenceResume> viabilisations,
        ReferenceResume typeBien,
        ReferenceResume standing,
        BigDecimal surfaceBatieM2,
        Integer anneeConstruction,
        Integer nombreNiveaux,
        Boolean locatifOuCommercial,
        BigDecimal loyerMensuelActuel,
        List<EquipementResponse> equipements,
        Instant dateCreation) {

    public static BienResponse depuis(Bien bien) {
        Bien reel = (Bien) Hibernate.unproxy(bien);
        BienBati bati = (reel instanceof BienBati b) ? b : null;
        return new BienResponse(
                reel.getId(), reel.getVersion(), reel.nature(),
                ReferenceResume.de(reel.getQuartier()),
                reel.getAdresse(), reel.getLatitude(), reel.getLongitude(), reel.getSurfaceTerrainM2(),
                ReferenceResume.de(reel.getTypeVoie()),
                ReferenceResume.de(reel.getTopographie()),
                ReferenceResume.de(reel.getStatutJuridique()),
                reel.getViabilisations().stream()
                        .sorted(Comparator.comparing(Viabilisation::getCode))
                        .map(ReferenceResume::de).toList(),
                bati == null ? null : ReferenceResume.de(bati.getTypeBien()),
                bati == null ? null : ReferenceResume.de(bati.getStanding()),
                bati == null ? null : bati.getSurfaceBatieM2(),
                bati == null ? null : bati.getAnneeConstruction(),
                bati == null ? null : bati.getNombreNiveaux(),
                bati == null ? null : bati.isLocatifOuCommercial(),
                bati == null ? null : bati.getLoyerMensuelActuel(),
                reel.getEquipements().stream().map(EquipementResponse::depuis).toList(),
                reel.getDateCreation());
    }
}
