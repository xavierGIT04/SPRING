package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.ParametreGlobal;

import java.util.Optional;

public interface ParametreGlobalRepository extends JpaRepository<ParametreGlobal, Long> {
    Optional<ParametreGlobal> findByCle(String cle);
}