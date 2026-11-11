package tg.nassara.estimation.service.marche;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.request.CreationAnnonceMarcheRequest;
import tg.nassara.estimation.dto.response.AnnonceMarcheResponse;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
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
}
