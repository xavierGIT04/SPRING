package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.PonderationMethode;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;

import java.util.List;
import java.util.Optional;

public interface PonderationMethodeRepository extends JpaRepository<PonderationMethode, Long> {

    Optional<PonderationMethode> findByMethodeAndNatureBien(MethodeEstimation methode, NatureBien natureBien);

    List<PonderationMethode> findByNatureBienAndActiveTrue(NatureBien natureBien);
}