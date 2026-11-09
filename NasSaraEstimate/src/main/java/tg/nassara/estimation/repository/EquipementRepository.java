package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.Equipement;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByBienId(Long bienId);
}