package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.Bien;

public interface BienRepository extends JpaRepository<Bien, Long> {
}