package tg.nassara.estimation.service.bien;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tg.nassara.estimation.dto.request.CreationBienRequest;
import tg.nassara.estimation.dto.request.EquipementRequest;
import tg.nassara.estimation.dto.response.BienResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.StatutJuridique;
import tg.nassara.estimation.entity.reference.Topographie;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.entity.reference.TypeEquipement;
import tg.nassara.estimation.entity.reference.TypeVoie;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.StandingRepository;
import tg.nassara.estimation.repository.StatutJuridiqueRepository;
import tg.nassara.estimation.repository.TopographieRepository;
import tg.nassara.estimation.repository.TypeBienRepository;
import tg.nassara.estimation.repository.TypeEquipementRepository;
import tg.nassara.estimation.repository.TypeVoieRepository;
import tg.nassara.estimation.repository.ViabilisationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests unitaires de la saisie des biens : validations et construction, sans base de données. */
class BienServiceTest {

    private BienRepository bienRepository;
    private QuartierRepository quartierRepository;
    private Quartier quartier;
    private BienService service;

    @BeforeEach
    void preparer() {
        bienRepository = mock(BienRepository.class);
        quartierRepository = mock(QuartierRepository.class);
        TypeVoieRepository voies = mock(TypeVoieRepository.class);
        TopographieRepository topographies = mock(TopographieRepository.class);
        StatutJuridiqueRepository statuts = mock(StatutJuridiqueRepository.class);
        ViabilisationRepository viabilisations = mock(ViabilisationRepository.class);
        TypeBienRepository typesBien = mock(TypeBienRepository.class);
        StandingRepository standings = mock(StandingRepository.class);
        TypeEquipementRepository equipements = mock(TypeEquipementRepository.class);

        quartier = new Quartier("TOKOIN", "Tokoin", bd("100000"));
        when(quartierRepository.findById(1L)).thenReturn(Optional.of(quartier));
        when(voies.findById(2L)).thenReturn(Optional.of(new TypeVoie("GOUDRON", "Goudron", bd("1.10"))));
        when(topographies.findById(3L)).thenReturn(Optional.of(new Topographie("PLAT", "Plat", bd("1.00"))));
        when(statuts.findById(4L)).thenReturn(Optional.of(new StatutJuridique("TF", "Titre Foncier", bd("1.20"))));
        when(viabilisations.findById(5L)).thenReturn(Optional.of(new Viabilisation("CEET", "CEET", bd("1.05"))));
        when(viabilisations.findById(6L)).thenReturn(Optional.of(new Viabilisation("TDE", "TdE", bd("1.05"))));
        when(typesBien.findById(7L)).thenReturn(Optional.of(new TypeBien("VILLA", "Villa", bd("1.10"))));
        when(standings.findById(8L)).thenReturn(Optional.of(
                new Standing("MOYEN", "Moyen", bd("250000"), bd("0.02"), bd("0.70"))));
        when(equipements.findById(9L)).thenReturn(Optional.of(
                new TypeEquipement("FORAGE", "Forage", bd("2500000"))));
        when(bienRepository.save(any(Bien.class))).thenAnswer(i -> i.getArgument(0));

        service = new BienService(bienRepository, quartierRepository, voies, topographies, statuts,
                viabilisations, typesBien, standings, equipements);
    }

    @Test
    void terrainNu_valide_estEnregistreAvecSesViabilisations() {
        CreationBienRequest requete = requete(NatureBien.TERRAIN, null, null, null, null, null, null,
                Set.of(5L, 6L), null);

        BienResponse reponse = service.creer(requete);

        assertThat(reponse.nature()).isEqualTo(NatureBien.TERRAIN);
        assertThat(reponse.quartier().code()).isEqualTo("TOKOIN");
        assertThat(reponse.viabilisations()).extracting("code").containsExactly("CEET", "TDE");
        assertThat(reponse.typeBien()).isNull();
        assertThat(reponse.locatifOuCommercial()).isNull();
        verify(bienRepository).save(any(TerrainNu.class));
    }

    @Test
    void bati_valide_avecEquipementAuMontantDuCatalogue() {
        CreationBienRequest requete = requete(NatureBien.BATI, 7L, 8L, bd("150"), anneeCourante() - 10, true,
                bd("450000"), null, List.of(new EquipementRequest(9L, null, "Forage profond")));

        BienResponse reponse = service.creer(requete);

        assertThat(reponse.nature()).isEqualTo(NatureBien.BATI);
        assertThat(reponse.standing().code()).isEqualTo("MOYEN");
        assertThat(reponse.locatifOuCommercial()).isTrue();
        assertThat(reponse.loyerMensuelActuel()).isEqualByComparingTo("450000");
        assertThat(reponse.equipements()).hasSize(1);
        assertThat(reponse.equipements().get(0).montantRetenu()).isEqualByComparingTo("2500000");
        verify(bienRepository).save(any(BienBati.class));
    }

    @Test
    void bati_champsObligatoiresAbsents_estRefuse() {
        CreationBienRequest requete = requete(NatureBien.BATI, 7L, null, bd("150"), null, false, null, null, null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("standingId")
                .hasMessageContaining("anneeConstruction");
        verify(bienRepository, never()).save(any());
    }

    @Test
    void terrainNu_avecChampsDuBati_estRefuse() {
        CreationBienRequest requete = requete(NatureBien.TERRAIN, 7L, 8L, bd("150"), 2010, null, null, null, null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("terrain nu")
                .hasMessageContaining("typeBienId");
    }

    @Test
    void bati_anneeDansLeFutur_estRefusee() {
        CreationBienRequest requete = requete(NatureBien.BATI, 7L, 8L, bd("150"), anneeCourante() + 1,
                false, null, null, null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("année de construction");
    }

    @Test
    void bati_loyerSansBienLocatif_estRefuse() {
        CreationBienRequest requete = requete(NatureBien.BATI, 7L, 8L, bd("150"), 2015, false,
                bd("450000"), null, null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("locatif");
    }

    @Test
    void referenceDesactivee_estRefusee() {
        quartier.setActif(false);
        CreationBienRequest requete = requete(NatureBien.TERRAIN, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("désactivé");
        verify(bienRepository, never()).save(any());
    }

    @Test
    void viabilisationInconnue_leveRessourceIntrouvable() {
        CreationBienRequest requete = requete(NatureBien.TERRAIN, null, null, null, null, null, null,
                Set.of(99L), null);

        assertThatThrownBy(() -> service.creer(requete))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Viabilisation");
    }

    @Test
    void bienIntrouvable_leveRessourceIntrouvable() {
        assertThatThrownBy(() -> service.obtenir(42L)).isInstanceOf(RessourceIntrouvableException.class);
    }

    // ---------- Fabriques ----------

    private static CreationBienRequest requete(NatureBien nature, Long typeBienId, Long standingId,
                                               BigDecimal surfaceBatie, Integer annee, Boolean locatif,
                                               BigDecimal loyer, Set<Long> viabilisations,
                                               List<EquipementRequest> equipements) {
        return new CreationBienRequest(nature, 1L, "Rue test", null, null, bd("500"), 2L, 3L, 4L,
                viabilisations, equipements, typeBienId, standingId, surfaceBatie, annee, null, locatif, loyer);
    }

    private static int anneeCourante() {
        return LocalDate.now(ZoneId.of("Africa/Lome")).getYear();
    }

    private static BigDecimal bd(String valeur) {
        return new BigDecimal(valeur);
    }
}
