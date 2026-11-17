package tg.nassara.estimation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tg.nassara.estimation.entity.ParametreGlobal;
import tg.nassara.estimation.enums.NiveauParametre;
import tg.nassara.estimation.repository.ParametreGlobalRepository;
import tg.nassara.estimation.service.estimation.CatalogueParametresGlobaux;
import tg.nassara.estimation.service.estimation.CatalogueParametresGlobaux.Entree;
import tg.nassara.estimation.service.parametrage.Acteur;
import tg.nassara.estimation.service.parametrage.ParametrageService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Au démarrage : crée les paramètres globaux manquants du catalogue (jamais d'écrasement d'une valeur existante).
 * Les réglages AVANCE sont toujours créés ; les réglages COURANT seulement si nassara.parametres.initialiser-courants=true.
 * Pour un paramètre existant sans description (créé avant le catalogue), seuls la description et le niveau sont complétés.
 */
@Component
public class InitialiseurParametresGlobaux implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(InitialiseurParametresGlobaux.class);
    private static final String TYPE = "parametres-globaux";

    private final ParametreGlobalRepository repository;
    private final ParametrageService parametrageService;
    private final boolean initialiserCourants;

    public InitialiseurParametresGlobaux(ParametreGlobalRepository repository,
                                         ParametrageService parametrageService,
                                         @Value("${nassara.parametres.initialiser-courants:false}") boolean initialiserCourants) {
        this.repository = repository;
        this.parametrageService = parametrageService;
        this.initialiserCourants = initialiserCourants;
    }

    @Override
    public void run(ApplicationArguments args) {
        Acteur acteur = Acteur.systeme();
        for (Entree entree : CatalogueParametresGlobaux.ENTREES) {
            try {
                Optional<ParametreGlobal> existant = repository.findByCle(entree.cle());
                if (existant.isPresent()) {
                    completer(existant.get(), entree, acteur);
                } else if (entree.niveau() == NiveauParametre.AVANCE || initialiserCourants) {
                    creer(entree, acteur);
                }
            } catch (RuntimeException e) {
                LOG.warn("Initialisation du paramètre {} impossible : {}", entree.cle(), e.getMessage());
            }
        }
    }

    private void creer(Entree e, Acteur acteur) {
        Map<String, String> valeurs = new LinkedHashMap<>();
        valeurs.put("cle", e.cle());
        valeurs.put("valeur", e.valeur());
        valeurs.put("unite", e.unite());
        valeurs.put("description", e.description());
        valeurs.put("niveau", e.niveau().name());
        parametrageService.creer(TYPE, valeurs, "Valeur initiale du catalogue", acteur);
    }

    private void completer(ParametreGlobal existant, Entree e, Acteur acteur) {
        if (existant.getDescription() != null) {
            return;
        }
        Map<String, String> valeurs = new LinkedHashMap<>();
        valeurs.put("description", e.description());
        valeurs.put("niveau", e.niveau().name());
        parametrageService.modifier(TYPE, existant.getId(), null, valeurs,
                "Description et niveau du catalogue (la valeur n'est pas modifiée)", acteur);
    }
}