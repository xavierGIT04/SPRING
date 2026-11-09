package tg.nassara.estimation.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Traduit les erreurs métier et techniques en réponses HTTP normalisées (RFC 9457). */
@RestControllerAdvice
public class GestionnaireExceptionsGlobal {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail introuvable(RessourceIntrouvableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflitException.class)
    public ProblemDetail conflit(ConflitException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DonneesInvalidesException.class)
    public ProblemDetail invalide(DonneesInvalidesException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integrite(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Contrainte d'intégrité violée (code déjà utilisé, ou valeur encore référencée).");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail verrouOptimiste(ObjectOptimisticLockingFailureException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Le paramètre a été modifié par quelqu'un d'autre. Rechargez-le puis réessayez.");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail enteteManquant(MissingRequestHeaderException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "En-tête obligatoire manquant : " + e.getHeaderName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
    
    @ExceptionHandler(ParametreManquantException.class)
    public ProblemDetail parametreManquant(ParametreManquantException e) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        probleme.setProperty("typeParametre", e.getTypeParametre());
        probleme.setProperty("cle", e.getCle());
        return probleme;
    }

    @ExceptionHandler(EstimationImpossibleException.class)
    public ProblemDetail estimationImpossible(EstimationImpossibleException e) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        probleme.setProperty("raisons", e.getRaisons());
        return probleme;
    }
}