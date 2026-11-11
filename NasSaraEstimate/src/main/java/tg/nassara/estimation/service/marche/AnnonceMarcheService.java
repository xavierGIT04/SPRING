package tg.nassara.estimation.service.marche;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.request.CreationAnnonceMarcheRequest;
import tg.nassara.estimation.dto.request.ModificationAnnonceMarcheRequest;
import tg.nassara.estimation.dto.response.AnnonceMarcheResponse;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.PeriodiciteLoyer;
import tg.nassara.estimation.enums.TypeAnnonce;
import tg.nassara.estimation.exception.ConflitException;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;
import tg.nassara.estimation.repository.AnnonceMarcheRepository;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.TypeBienRepository;
import tg.nassara.estimation.service.estimation.ClesParametres;
import tg.nassara.estimation.service.estimation.ParametresCalcul;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.Convertisseurs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/** Saisie manuelle des annonces trouvées par les agents. */
@Service
public class AnnonceMarcheService {

    private static final ZoneId FUSEAU = ZoneId.of("Africa/Lome");

    private final AnnonceMarcheRepository annonceRepository;
    private final QuartierRepository quartierRepository;
    private final TypeBienRepository typeBienRepository;
    private final ParametresCalcul parametres;

    public AnnonceMarcheService(AnnonceMarcheRepository annonceRepository, QuartierRepository quartierRepository,
                                TypeBienRepository typeBienRepository, ParametresCalcul parametres) {
        this.annonceRepository = annonceRepository;
        this.quartierRepository = quartierRepository;
        this.typeBienRepository = typeBienRepository;
        this.parametres = parametres;
    }

    @Transactional
    public AnnonceMarcheResponse creer(CreationAnnonceMarcheRequest requete, Acteur acteur) {
        Quartier quartier = quartierRepository.findById(requete.quartierId())
                .orElseThrow(() -> new RessourceIntrouvableException("Quartier introuvable : " + requete.quartierId()));
        if (!quartier.isActif()) {
            throw new DonneesInvalidesException("Le quartier « " + quartier.getLibelle() + " » est désactivé.");
        }
        String url = Convertisseurs.texteOptionnel(requete.urlSource());
        if (url != null && annonceRepository.existsByUrlSource(url)) {
            throw new ConflitException("Une annonce avec cette URL existe déjà.");
        }
        if (requete.surfaceTerrainM2() != null && requete.nombreLots() != null) {
            throw new DonneesInvalidesException("Renseignez la surface en m² OU le nombre de lots, pas les deux.");
        }
        BigDecimal surfaceTerrain = requete.surfaceTerrainM2();
        if (surfaceTerrain == null && requete.nombreLots() != null) {
            surfaceTerrain = requete.nombreLots().multiply(parametres.valeur(ClesParametres.MARCHE_SURFACE_LOT_M2));
        }
        if (requete.typeAnnonce() == TypeAnnonce.VENTE && surfaceTerrain == null) {
            throw new DonneesInvalidesException(
                    "Pour une vente, indiquez la surface du terrain (m²) ou le nombre de lots.");
        }
        
        PeriodiciteLoyer periodicite = null;
        Boolean meuble = null;
        if (requete.typeAnnonce() == TypeAnnonce.LOCATION) {
            if (requete.nature() != NatureBien.BATI) {
                throw new DonneesInvalidesException("Seules les locations de biens bâtis sont prises en compte.");
            }
            periodicite = requete.periodicite() != null ? requete.periodicite() : PeriodiciteLoyer.MENSUEL;
            meuble = requete.meuble();
        } else if (requete.periodicite() != null || requete.meuble() != null) {
            throw new DonneesInvalidesException("La périodicité et l'indicateur meublé ne concernent que les locations.");
        }
        
        TypeBien typeBien = requete.typeBienId() == null ? null
                : typeBienRepository.findById(requete.typeBienId()).orElseThrow(
                        () -> new RessourceIntrouvableException("Type de bien introuvable : " + requete.typeBienId()));

        AnnonceMarche annonce = new AnnonceMarche();
        annonce.setSource(requete.source().strip());
        annonce.setOrigine(OrigineAnnonce.SAISIE_AGENT);
        annonce.setSaisiePar(acteur.identifiant());
        annonce.setUrlSource(url);
        annonce.setTitre(Convertisseurs.texteOptionnel(requete.titre()));
        annonce.setTypeAnnonce(requete.typeAnnonce());
        annonce.setNature(requete.nature());
        annonce.setTypeBien(typeBien);
        annonce.setQuartier(quartier);
        annonce.setPeriodicite(periodicite);
        annonce.setMeuble(meuble);
        annonce.setQuartierBrut(quartier.getLibelle());
        annonce.setSurfaceTerrainM2(surfaceTerrain);
        annonce.setSurfaceBatieM2(requete.surfaceBatieM2());
        annonce.setMontant(requete.montant());
        annonce.setDatePublication(requete.datePublication());
        annonce.setDateCollecte(LocalDate.now(FUSEAU));
        annonce.setActif(true);
        return AnnonceMarcheResponse.depuis(annonceRepository.save(annonce));
    }

    @Transactional(readOnly = true)
    public List<AnnonceMarcheResponse> lister(Long quartierId, boolean actifsSeulement) {
        List<AnnonceMarche> annonces = quartierId == null
                ? annonceRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCollecte"))
                : annonceRepository.findByQuartierIdOrderByDateCollecteDesc(quartierId);
        return annonces.stream()
                .filter(a -> !actifsSeulement || a.isActif())
                .map(AnnonceMarcheResponse::depuis)
                .toList();
    }

    /** Jamais de suppression : une annonce erronée ou périmée est désactivée et sort des comparaisons. */
    @Transactional
    public AnnonceMarcheResponse desactiver(Long id) {
        AnnonceMarche annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Annonce introuvable : " + id));
        annonce.setActif(false);
        return AnnonceMarcheResponse.depuis(annonceRepository.save(annonce));
    }
    
    /** Correction d'une saisie manuelle ; une annonce du catalogue est réécrite à chaque actualisation, donc non modifiable. */
    @Transactional
    public AnnonceMarcheResponse modifier(Long id, ModificationAnnonceMarcheRequest requete) {
        AnnonceMarche annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Annonce introuvable : " + id));
        if (annonce.getOrigine() != OrigineAnnonce.SAISIE_AGENT) {
            throw new DonneesInvalidesException("Une annonce du catalogue est réécrite à chaque actualisation : "
                    + "seules les saisies manuelles sont modifiables.");
        }
        if (requete.version() != null && !requete.version().equals(annonce.getVersion())) {
            throw new ConflitException("L'annonce a été modifiée par quelqu'un d'autre. Rechargez-la puis réessayez.");
        }
        if (requete.montant() == null && requete.surfaceTerrainM2() == null && requete.surfaceBatieM2() == null
                && requete.periodicite() == null && requete.meuble() == null) {
            throw new DonneesInvalidesException("Aucune valeur à modifier.");
        }
        if (annonce.getTypeAnnonce() != TypeAnnonce.LOCATION
                && (requete.periodicite() != null || requete.meuble() != null)) {
            throw new DonneesInvalidesException("La périodicité et l'indicateur meublé ne concernent que les locations.");
        }
        if (requete.montant() != null) {
            annonce.setMontant(requete.montant());
        }
        if (requete.surfaceTerrainM2() != null) {
            annonce.setSurfaceTerrainM2(requete.surfaceTerrainM2());
        }
        if (requete.surfaceBatieM2() != null) {
            annonce.setSurfaceBatieM2(requete.surfaceBatieM2());
        }
        if (requete.periodicite() != null) {
            annonce.setPeriodicite(requete.periodicite());
        }
        if (requete.meuble() != null) {
            annonce.setMeuble(requete.meuble());
        }
        annonce.setDateCollecte(LocalDate.now(FUSEAU)); // une correction vaut vérification
        return AnnonceMarcheResponse.depuis(annonceRepository.save(annonce));
    }
}
