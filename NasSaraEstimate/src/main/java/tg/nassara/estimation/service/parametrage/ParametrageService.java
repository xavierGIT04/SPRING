package tg.nassara.estimation.service.parametrage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.dto.response.HistoriqueParametreResponse;
import tg.nassara.estimation.dto.response.PageResponse;
import tg.nassara.estimation.dto.response.ParametreResponse;
import tg.nassara.estimation.dto.response.TypeParametreResponse;
import tg.nassara.estimation.entity.EntiteBase;
import tg.nassara.estimation.entity.HistoriqueParametre;
import tg.nassara.estimation.enums.TypeOperation;
import tg.nassara.estimation.exception.ConflitException;
import tg.nassara.estimation.exception.DonneesInvalidesException;
import tg.nassara.estimation.exception.RessourceIntrouvableException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Cœur du paramétrage : création, modification, restauration et consultation de tous les paramètres,
 * avec historisation systématique (qui, quand, ancienne et nouvelle valeur).
 */
@Service
public class ParametrageService {

    private final RegistreParametres registre;
    private final HistoriqueParametreService historique;

    public ParametrageService(RegistreParametres registre, HistoriqueParametreService historique) {
        this.registre = registre;
        this.historique = historique;
    }

    // ---------- Consultation ----------

    @Transactional(readOnly = true)
    public List<TypeParametreResponse> types() {
        return registre.toutes().stream()
                .map(d -> new TypeParametreResponse(
                        d.type(),
                        d.champs().stream().filter(c -> !c.immuable()).map(c -> c.nom()).toList(),
                        d.champs().stream().filter(c -> c.immuable()).map(c -> c.nom()).toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParametreResponse> lister(String type, boolean actifsSeulement) {
        return listerAvec(registre.parType(type), actifsSeulement);
    }

    @Transactional(readOnly = true)
    public ParametreResponse obtenir(String type, Long id) {
        return obtenirAvec(registre.parType(type), id);
    }

    @Transactional(readOnly = true)
    public PageResponse<HistoriqueParametreResponse> historique(String type, Long id, int page, int taille) {
        return historique.historiqueDe(registre.parType(type).nomEntite(), id, page, taille);
    }

    @Transactional(readOnly = true)
    public PageResponse<HistoriqueParametreResponse> historiqueGlobal(int page, int taille) {
        return historique.historiqueGlobal(page, taille);
    }

    // ---------- Écriture ----------

    @Transactional
    public ParametreResponse creer(String type, Map<String, String> valeurs, String motif, Acteur acteur) {
        return creerAvec(registre.parType(type), valeurs, motif, acteur);
    }

    @Transactional
    public ParametreResponse modifier(String type, Long id, Long versionAttendue,
                                      Map<String, String> valeurs, String motif, Acteur acteur) {
        return modifierAvec(registre.parType(type), id, versionAttendue, valeurs, motif, acteur);
    }

    /** Rétablit l'ancienne valeur d'une ligne d'historique ; l'opération est elle-même historisée. */
    @Transactional
    public ParametreResponse restaurer(Long historiqueId, String motif, Acteur acteur) {
        HistoriqueParametre ligne = historique.trouver(historiqueId);
        if (ligne.getOperation() == TypeOperation.CREATION || ligne.getChamp() == null) {
            throw new DonneesInvalidesException(
                    "Seule la modification d'un champ peut être restaurée. Pour annuler une création, désactivez le paramètre.");
        }
        return restaurerAvec(registre.parNomEntite(ligne.getTypeParametre()), ligne, motif, acteur);
    }

    // ---------- Implémentations génériques (capture du type T) ----------

    private <T extends EntiteBase> List<ParametreResponse> listerAvec(DefinitionParametre<T> definition,
                                                                      boolean actifsSeulement) {
        return definition.repository().findAll().stream()
                .filter(e -> !actifsSeulement || definition.estActif(e))
                .sorted(Comparator.comparing(EntiteBase::getId))
                .map(e -> versReponse(definition, e))
                .toList();
    }

    private <T extends EntiteBase> ParametreResponse obtenirAvec(DefinitionParametre<T> definition, Long id) {
        return versReponse(definition, charger(definition, id));
    }

    private <T extends EntiteBase> ParametreResponse creerAvec(DefinitionParametre<T> definition,
                                                               Map<String, String> valeurs,
                                                               String motif, Acteur acteur) {
        ValeursSaisies saisies = new ValeursSaisies(valeurs);
        for (String nom : saisies.noms()) {
            if (definition.champ(nom).isEmpty()) {
                throw new DonneesInvalidesException("Champ inconnu pour '" + definition.type() + "' : " + nom);
            }
        }
        T entite = definition.fabriquer(saisies);
        // Réapplique chaque champ fourni : déclenche les validations (positif, booléen, etc.) avant l'enregistrement.
        for (String nom : saisies.noms()) {
            definition.champ(nom).orElseThrow().ecriveur().accept(entite, saisies.valeurBrute(nom));
        }
        T enregistre = definition.repository().saveAndFlush(entite);
        historique.enregistrer(definition.nomEntite(), enregistre.getId(), definition.reference(enregistre),
                TypeOperation.CREATION, null, null, definition.resume(enregistre), acteur, motif, null);
        return versReponse(definition, enregistre);
    }

    private <T extends EntiteBase> ParametreResponse modifierAvec(DefinitionParametre<T> definition, Long id,
                                                                  Long versionAttendue,
                                                                  Map<String, String> valeurs,
                                                                  String motif, Acteur acteur) {
        if (valeurs == null || valeurs.isEmpty()) {
            throw new DonneesInvalidesException("Aucune valeur à modifier.");
        }
        T entite = charger(definition, id);
        if (versionAttendue != null && !versionAttendue.equals(entite.getVersion())) {
            throw new ConflitException("Le paramètre a été modifié par quelqu'un d'autre. Rechargez-le puis réessayez.");
        }
        for (Map.Entry<String, String> entree : valeurs.entrySet()) {
            ChampParametre<T> champ = definition.champ(entree.getKey())
                    .orElseThrow(() -> new DonneesInvalidesException("Champ inconnu : " + entree.getKey()));
            if (champ.immuable()) {
                throw new DonneesInvalidesException("Le champ '" + champ.nom() + "' n'est pas modifiable.");
            }
            String ancienne = Convertisseurs.versTexte(champ.lecteur().apply(entite));
            champ.ecriveur().accept(entite, entree.getValue());
            String nouvelle = Convertisseurs.versTexte(champ.lecteur().apply(entite));
            if (!Objects.equals(ancienne, nouvelle)) {
                TypeOperation operation = (champ.activation() && "false".equals(nouvelle))
                        ? TypeOperation.DESACTIVATION : TypeOperation.MODIFICATION;
                historique.enregistrer(definition.nomEntite(), entite.getId(), definition.reference(entite),
                        operation, champ.nom(), ancienne, nouvelle, acteur, motif, null);
            }
        }
        return versReponse(definition, definition.repository().saveAndFlush(entite));
    }

    private <T extends EntiteBase> ParametreResponse restaurerAvec(DefinitionParametre<T> definition,
                                                                   HistoriqueParametre ligne,
                                                                   String motif, Acteur acteur) {
        T entite = charger(definition, ligne.getIdParametre());
        ChampParametre<T> champ = definition.champ(ligne.getChamp())
                .orElseThrow(() -> new DonneesInvalidesException("Champ inconnu : " + ligne.getChamp()));
        String courante = Convertisseurs.versTexte(champ.lecteur().apply(entite));
        String cible = ligne.getAncienneValeur();
        if (Objects.equals(courante, cible)) {
            throw new ConflitException("Le paramètre a déjà cette valeur.");
        }
        champ.ecriveur().accept(entite, cible);
        historique.enregistrer(definition.nomEntite(), entite.getId(), definition.reference(entite),
                TypeOperation.RESTAURATION, champ.nom(), courante, cible, acteur, motif, ligne.getId());
        return versReponse(definition, definition.repository().saveAndFlush(entite));
    }

    private <T extends EntiteBase> T charger(DefinitionParametre<T> definition, Long id) {
        return definition.repository().findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(definition.nomEntite() + " introuvable : " + id));
    }

    private <T extends EntiteBase> ParametreResponse versReponse(DefinitionParametre<T> definition, T entite) {
        Map<String, Object> valeurs = new LinkedHashMap<>();
        for (ChampParametre<T> champ : definition.champs()) {
            valeurs.put(champ.nom(), champ.lecteur().apply(entite));
        }
        return new ParametreResponse(entite.getId(), definition.type(), entite.getVersion(), valeurs);
    }
}