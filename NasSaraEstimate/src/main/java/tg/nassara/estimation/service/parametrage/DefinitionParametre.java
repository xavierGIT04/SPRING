package tg.nassara.estimation.service.parametrage;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.EntiteBase;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Description complète d'un type de paramètre gérable par les agents. */
public final class DefinitionParametre<T extends EntiteBase> {

    private final String type;
    private final String nomEntite;
    private final JpaRepository<T, Long> repository;
    private final Function<T, String> referenceur;
    private final List<ChampParametre<T>> champs;
    private final Function<ValeursSaisies, T> fabrique;

    /**
     * @param type        identifiant dans l'URL (ex. "standings")
     * @param nomEntite   nom stocké dans l'historique (ex. "Standing")
     * @param referenceur texte lisible identifiant une instance (code, clé, etc.)
     * @param fabrique    construit une instance à partir des champs obligatoires
     */
    public DefinitionParametre(String type, String nomEntite, JpaRepository<T, Long> repository,
                               Function<T, String> referenceur, List<ChampParametre<T>> champs,
                               Function<ValeursSaisies, T> fabrique) {
        this.type = type;
        this.nomEntite = nomEntite;
        this.repository = repository;
        this.referenceur = referenceur;
        this.champs = List.copyOf(champs);
        this.fabrique = fabrique;
    }

    public String type() { return type; }
    public String nomEntite() { return nomEntite; }
    public JpaRepository<T, Long> repository() { return repository; }
    public List<ChampParametre<T>> champs() { return champs; }

    public Optional<ChampParametre<T>> champ(String nom) {
        return champs.stream().filter(c -> c.nom().equals(nom)).findFirst();
    }

    public T fabriquer(ValeursSaisies valeurs) {
        return fabrique.apply(valeurs);
    }

    public String reference(T entite) {
        return referenceur.apply(entite);
    }

    /** Sans champ d'activation, le paramètre est considéré comme toujours actif. */
    public boolean estActif(T entite) {
        return champs.stream()
                .filter(ChampParametre::activation)
                .findFirst()
                .map(c -> Boolean.TRUE.equals(c.lecteur().apply(entite)))
                .orElse(true);
    }

    /** Résumé « champ=valeur » stocké dans l'historique lors d'une création. */
    public String resume(T entite) {
        String texte = champs.stream()
                .map(c -> c.nom() + "=" + Convertisseurs.versTexte(c.lecteur().apply(entite)))
                .collect(Collectors.joining("; "));
        return texte.length() > 1000 ? texte.substring(0, 1000) : texte;
    }
}