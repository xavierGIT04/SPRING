package tg.nassara.estimation.repository;

import tg.nassara.estimation.entity.reference.Quartier;

import java.util.List;
import java.util.Optional;

public interface QuartierRepository extends ReferenceRepository<Quartier> {

    Optional<Quartier> findByLibelleIgnoreCase(String libelle);

    /** Recherche insensible aux accents et à la casse : le fragment doit être normalisé comme un code. */
    List<Quartier> findByCodeContainingAndActifTrueOrderByLibelleAsc(String fragmentNormalise);
}