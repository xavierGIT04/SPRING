package tg.nassara.estimation.service.parametrage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.request.CreationQuartierRequest;
import tg.nassara.estimation.dto.response.ParametreResponse;
import tg.nassara.estimation.dto.response.QuartierResume;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.exception.ConflitException;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.repository.QuartierRepository;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Création rapide d'un quartier inconnu et recherche tolérante (accents, casse). */
@Service
public class QuartierService {

    private static final String TYPE_QUARTIER = "quartiers";

    private final QuartierRepository quartierRepository;
    private final ParametrageService parametrageService;

    public QuartierService(QuartierRepository quartierRepository, ParametrageService parametrageService) {
        this.quartierRepository = quartierRepository;
        this.parametrageService = parametrageService;
    }

    /** Le prix de base saisi est marqué « provisoire » jusqu'à confirmation par un agent habilité. */
    public ParametreResponse creerRapidement(CreationQuartierRequest requete, Acteur acteur) {
        String libelle = requete.libelle().strip();
        String code = genererCode(libelle);
        if (quartierRepository.existsByCodeIgnoreCase(code)) {
            throw new ConflitException("Le quartier « " + libelle + " » existe déjà.");
        }
        Map<String, String> valeurs = new LinkedHashMap<>();
        valeurs.put("code", code);
        valeurs.put("libelle", libelle);
        valeurs.put("prixBaseTerrainM2", requete.prixBaseTerrainM2().toPlainString());
        valeurs.put("prixBaseProvisoire", "true");
        return parametrageService.creer(TYPE_QUARTIER, valeurs,
                "Création rapide lors d'une demande d'estimation", acteur);
    }

    @Transactional(readOnly = true)
    public List<QuartierResume> rechercher(String recherche) {
        String fragment = normaliser(recherche);
        List<Quartier> trouves = fragment.isEmpty()
                ? quartierRepository.findByActifTrueOrderByLibelleAsc()
                : quartierRepository.findByCodeContainingAndActifTrueOrderByLibelleAsc(fragment);
        return trouves.stream()
                .map(q -> new QuartierResume(q.getId(), q.getCode(), q.getLibelle(),
                        q.getPrixBaseTerrainM2(), q.isPrixBaseProvisoire()))
                .toList();
    }

    static String genererCode(String libelle) {
        String code = normaliser(libelle);
        if (code.isEmpty()) {
            throw new DonneesInvalidesException("Libellé de quartier invalide.");
        }
        return code.length() > 60 ? code.substring(0, 60) : code;
    }

    /** « Tokoïn Ouest » devient « TOKOIN-OUEST » : sans accents, majuscules, tirets. */
    public static String normaliser(String texte) {
        if (texte == null) {
            return "";
        }
        String sansAccents = Normalizer.normalize(texte, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sansAccents.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-").replaceAll("^-+|-+$", "");
    }
}