package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.Estimation;

import java.util.List;

public interface EstimationRepository extends JpaRepository<Estimation, Long> {
    List<Estimation> findByBienIdOrderByDateCreationDesc(Long bienId);
}