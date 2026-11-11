package tg.nassara.estimation.service.marche;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tg.nassara.estimation.config.ConfigurationCatalogue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Actualise les annonces du catalogue avant une estimation.
 *
 * <p>À appeler HORS de toute transaction (l'orchestrateur, étape 7) : la lecture HTTP ne doit pas garder une
 * connexion de base ouverte. Un court cache évite de relire le site à chaque requête ; si le site est
 * injoignable, l'estimation continue avec les dernières annonces enregistrées et le résultat l'indique.
 */
@Service
public class ServiceSynchronisationCatalogue {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceSynchronisationCatalogue.class);

    private final SourceCatalogueNasSarra source;
    private final PersisteurAnnoncesCatalogue persisteur;
    private final ConfigurationCatalogue.Proprietes proprietes;

    private Instant derniereTentative;
    private ResultatSynchronisation dernierResultat;

    public ServiceSynchronisationCatalogue(SourceCatalogueNasSarra source, PersisteurAnnoncesCatalogue persisteur,
                                           ConfigurationCatalogue.Proprietes proprietes) {
        this.source = source;
        this.persisteur = persisteur;
        this.proprietes = proprietes;
    }

    public synchronized ResultatSynchronisation synchroniserSiNecessaire() {
        Instant maintenant = Instant.now();
        if (derniereTentative != null && dernierResultat != null
                && Duration.between(derniereTentative, maintenant).toMinutes() < proprietes.dureeCacheMinutes()) {
            ResultatSynchronisation r = dernierResultat;
            return new ResultatSynchronisation(ResultatSynchronisation.Statut.CACHE_VALIDE, r.annoncesLues(),
                    r.annoncesRetenues(), r.annoncesDesactivees(), r.date(), "Annonces du catalogue récentes (cache).");
        }
        return executer(maintenant);
    }

    /** Force la lecture (bouton « actualiser » côté CRM). */
    public synchronized ResultatSynchronisation forcer() {
        return executer(Instant.now());
    }

    private ResultatSynchronisation executer(Instant maintenant) {
        ResultatSynchronisation resultat;
        try {
            List<AnnonceCollectee> lues = source.collecter();
            if (lues.isEmpty()) {
                throw new IllegalStateException("aucune annonce reconnue dans la page (structure du site modifiée ?).");
            }
            PersisteurAnnoncesCatalogue.Bilan bilan = persisteur.appliquer(source.nom(), lues);
            resultat = new ResultatSynchronisation(ResultatSynchronisation.Statut.ACTUALISEE, lues.size(),
                    bilan.retenues(), bilan.desactivees(), maintenant, "Catalogue actualisé.");
        } catch (RuntimeException e) {
            LOG.warn("Synchronisation du catalogue en échec : {}", e.getMessage());
            resultat = new ResultatSynchronisation(ResultatSynchronisation.Statut.ECHEC, 0, 0, 0, maintenant,
                    "Catalogue non actualisé (" + e.getMessage()
                            + ") : les dernières annonces enregistrées sont utilisées.");
        }
        derniereTentative = maintenant;
        dernierResultat = resultat;
        return resultat;
    }
}
