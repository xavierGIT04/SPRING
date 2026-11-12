package tg.nassara.estimation.service.bien;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.request.CreationBienRequest;
import tg.nassara.estimation.dto.request.EquipementRequest;
import tg.nassara.estimation.dto.response.BienResponse;
import tg.nassara.estimation.dto.response.BienResume;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.entity.Bien;
import tg.nassara.estimation.entity.BienBati;
import tg.nassara.estimation.entity.Equipement;
import tg.nassara.estimation.entity.TerrainNu;
import tg.nassara.estimation.entity.reference.EntiteReference;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.BienRepository;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.ReferenceRepository;
import tg.nassara.estimation.repository.StandingRepository;
import tg.nassara.estimation.repository.StatutJuridiqueRepository;
import tg.nassara.estimation.repository.TopographieRepository;
import tg.nassara.estimation.repository.TypeBienRepository;
import tg.nassara.estimation.repository.TypeEquipementRepository;
import tg.nassara.estimation.repository.TypeVoieRepository;
import tg.nassara.estimation.repository.ViabilisationRepository;
import tg.nassara.estimation.service.parametrage.Convertisseurs;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Saisie des biens par les agents. Les contrôles de cohérence (champs obligatoires du bâti, champs interdits pour un
 * terrain nu) sont faits ici, avant l'enregistrement, pour que l'agent soit averti à la saisie et non au calcul.
 */
@Service
public class BienService {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");
    private static final int ANNEE_CONSTRUCTION_MIN = 1800;

    private final BienRepository bienRepository;
    private final QuartierRepository quartierRepository;
    private final TypeVoieRepository typeVoieRepository;
    private final TopographieRepository topographieRepository;
    private final StatutJuridiqueRepository statutJuridiqueRepository;
    private final ViabilisationRepository viabilisationRepository;
    private final TypeBienRepository typeBienRepository;
    private final StandingRepository standingRepository;
    private final TypeEquipementRepository typeEquipementRepository;

    public BienService(BienRepository bienRepository, QuartierRepository quartierRepository,
                       TypeVoieRepository typeVoieRepository, TopographieRepository topographieRepository,
                       StatutJuridiqueRepository statutJuridiqueRepository,
                       ViabilisationRepository viabilisationRepository, TypeBienRepository typeBienRepository,
                       StandingRepository standingRepository, TypeEquipementRepository typeEquipementRepository) {
        this.bienRepository = bienRepository;
        this.quartierRepository = quartierRepository;
        this.typeVoieRepository = typeVoieRepository;
        this.topographieRepository = topographieRepository;
        this.statutJuridiqueRepository = statutJuridiqueRepository;
        this.viabilisationRepository = viabilisationRepository;
        this.typeBienRepository = typeBienRepository;
        this.standingRepository = standingRepository;
        this.typeEquipementRepository = typeEquipementRepository;
    }

    @Transactional
    public BienResponse creer(CreationBienRequest requete) {
        validerCoherence(requete);

        Bien bien;
        if (requete.nature() == NatureBien.BATI) {
            BienBati bati = new BienBati();
            bati.setTypeBien(reference(typeBienRepository, requete.typeBienId(), "Type de bien"));
            bati.setStanding(reference(standingRepository, requete.standingId(), "Standing"));
            bati.setSurfaceBatieM2(requete.surfaceBatieM2());
            bati.setAnneeConstruction(requete.anneeConstruction());
            bati.setNombreNiveaux(requete.nombreNiveaux());
            bati.setLocatifOuCommercial(Boolean.TRUE.equals(requete.locatifOuCommercial()));
            bati.setLoyerMensuelActuel(requete.loyerMensuelActuel());
            bien = bati;
        } else {
            bien = new TerrainNu();
        }

        bien.setQuartier(reference(quartierRepository, requete.quartierId(), "Quartier"));
        bien.setAdresse(Convertisseurs.texteOptionnel(requete.adresse()));
        bien.setLatitude(requete.latitude());
        bien.setLongitude(requete.longitude());
        bien.setSurfaceTerrainM2(requete.surfaceTerrainM2());
        bien.setTypeVoie(reference(typeVoieRepository, requete.typeVoieId(), "Type de voie"));
        bien.setTopographie(reference(topographieRepository, requete.topographieId(), "Topographie"));
        bien.setStatutJuridique(reference(statutJuridiqueRepository, requete.statutJuridiqueId(), "Statut juridique"));

        Set<Viabilisation> viabilisations = new HashSet<>();
        Set<Long> identifiants = requete.viabilisationIds() == null
                ? Set.of() : new LinkedHashSet<>(requete.viabilisationIds());
        for (Long id : identifiants) {
            viabilisations.add(reference(viabilisationRepository, id, "Viabilisation"));
        }
        bien.setViabilisations(viabilisations);

        if (requete.equipements() != null) {
            for (EquipementRequest e : requete.equipements()) {
                bien.ajouterEquipement(new Equipement(
                        reference(typeEquipementRepository, e.typeEquipementId(), "Type d'équipement"),
                        e.montant(), Convertisseurs.texteOptionnel(e.description())));
            }
        }
        return BienResponse.depuis(bienRepository.save(bien));
    }

    @Transactional(readOnly = true)
    public BienResponse obtenir(Long id) {
        return BienResponse.depuis(bienRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Bien introuvable : " + id)));
    }

    // ---------- Contrôles ----------

    private void validerCoherence(CreationBienRequest r) {
        if ((r.latitude() == null) != (r.longitude() == null)) {
            throw new DonneesInvalidesException("La latitude et la longitude vont ensemble : renseignez les deux ou aucune.");
        }
        if (r.nature() == NatureBien.BATI) {
            List<String> manquants = new ArrayList<>();
            if (r.typeBienId() == null) manquants.add("typeBienId");
            if (r.standingId() == null) manquants.add("standingId");
            if (r.surfaceBatieM2() == null) manquants.add("surfaceBatieM2");
            if (r.anneeConstruction() == null) manquants.add("anneeConstruction");
            if (!manquants.isEmpty()) {
                throw new DonneesInvalidesException(
                        "Pour un bien bâti, les champs suivants sont obligatoires : " + String.join(", ", manquants) + ".");
            }
            int anneeCourante = LocalDate.now(FUSEAU).getYear();
            if (r.anneeConstruction() < ANNEE_CONSTRUCTION_MIN || r.anneeConstruction() > anneeCourante) {
                throw new DonneesInvalidesException("L'année de construction doit être comprise entre "
                        + ANNEE_CONSTRUCTION_MIN + " et " + anneeCourante + " (" + r.anneeConstruction() + ").");
            }
            if (r.loyerMensuelActuel() != null && !Boolean.TRUE.equals(r.locatifOuCommercial())) {
                throw new DonneesInvalidesException(
                        "Un loyer actuel n'a de sens que pour un bien locatif ou commercial : "
                                + "indiquez locatifOuCommercial = true ou retirez le loyer.");
            }
        } else {
            List<String> interdits = new ArrayList<>();
            if (r.typeBienId() != null) interdits.add("typeBienId");
            if (r.standingId() != null) interdits.add("standingId");
            if (r.surfaceBatieM2() != null) interdits.add("surfaceBatieM2");
            if (r.anneeConstruction() != null) interdits.add("anneeConstruction");
            if (r.nombreNiveaux() != null) interdits.add("nombreNiveaux");
            if (r.loyerMensuelActuel() != null) interdits.add("loyerMensuelActuel");
            if (Boolean.TRUE.equals(r.locatifOuCommercial())) interdits.add("locatifOuCommercial");
            if (!interdits.isEmpty()) {
                throw new DonneesInvalidesException(
                        "Un terrain nu ne peut pas porter : " + String.join(", ", interdits) + ".");
            }
        }
    }

    /** Une valeur de référence doit exister et être active : une valeur désactivée ne sert plus à de nouveaux biens. */
    private <T extends EntiteReference> T reference(ReferenceRepository<T> repository, Long id, String libelle) {
        T valeur = repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(libelle + " introuvable : " + id));
        if (!valeur.isActif()) {
            throw new DonneesInvalidesException(libelle + " « " + valeur.getLibelle() + " » est désactivé(e).");
        }
        return valeur;
    }
    
    @Transactional(readOnly = true)
    public PageResponse<BienResume> lister(int page, int taille) {
        Page<Bien> resultat = bienRepository.findAllByOrderByDateCreationDesc(
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(taille, 1), 100)));
        return new PageResponse<>(resultat.getContent().stream().map(BienResume::depuis).toList(),
                resultat.getNumber(), resultat.getSize(), resultat.getTotalElements(), resultat.getTotalPages());
    }
}
