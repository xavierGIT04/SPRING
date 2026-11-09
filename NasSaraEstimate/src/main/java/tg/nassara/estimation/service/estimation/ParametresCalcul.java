package tg.nassara.estimation.service.estimation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.nassara.estimation.entity.ParametreGlobal;
import tg.nassara.estimation.entity.PonderationMethode;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.exception.ParametreManquantException;
import tg.nassara.estimation.repository.ParametreGlobalRepository;
import tg.nassara.estimation.repository.PonderationMethodeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Point d'accès unique des méthodes aux paramètres de calcul : lecture en base à chaque estimation
 * (une modification par un agent s'applique immédiatement), et erreur explicite si une valeur manque.
 */
@Service
@Transactional(readOnly = true)
public class ParametresCalcul {

    private final ParametreGlobalRepository parametreGlobalRepository;
    private final PonderationMethodeRepository ponderationRepository;

    public ParametresCalcul(ParametreGlobalRepository parametreGlobalRepository,
                            PonderationMethodeRepository ponderationRepository) {
        this.parametreGlobalRepository = parametreGlobalRepository;
        this.ponderationRepository = ponderationRepository;
    }

    public BigDecimal valeur(String cle) {
        return parametreGlobalRepository.findByCle(cle)
                .map(ParametreGlobal::getValeur)
                .orElseThrow(() -> new ParametreManquantException("ParametreGlobal", cle));
    }

    public int entier(String cle) {
        return valeur(cle).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    public Optional<PonderationMethode> ponderationActive(MethodeEstimation methode, NatureBien nature) {
        return ponderationRepository.findByMethodeAndNatureBien(methode, nature)
                .filter(PonderationMethode::isActive);
    }

    /** Méthodes activées pour une nature de bien (pondération active). */
    public List<PonderationMethode> ponderationsActives(NatureBien nature) {
        return ponderationRepository.findByNatureBienAndActiveTrue(nature);
    }
}