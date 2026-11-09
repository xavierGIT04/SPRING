package tg.nassara.estimation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.HistoriqueParametre;

public interface HistoriqueParametreRepository extends JpaRepository<HistoriqueParametre, Long> {

    Page<HistoriqueParametre> findByTypeParametreAndIdParametreOrderByDateModificationDesc(
            String typeParametre, Long idParametre, Pageable pageable);

    Page<HistoriqueParametre> findAllByOrderByDateModificationDesc(Pageable pageable);
}