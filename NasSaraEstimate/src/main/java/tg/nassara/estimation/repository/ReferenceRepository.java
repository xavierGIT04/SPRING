package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import tg.nassara.estimation.entity.reference.EntiteReference;

import java.util.List;
import java.util.Optional;

/** Contrat commun à tous les référentiels gérés par les agents. */
@NoRepositoryBean
public interface ReferenceRepository<T extends EntiteReference> extends JpaRepository<T, Long> {

    Optional<T> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    List<T> findByActifTrueOrderByLibelleAsc();
}