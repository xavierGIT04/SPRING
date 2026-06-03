package assara.group.ossaraanalytics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistException.class)
    public final ResponseEntity<ErrorMessage> handleResourceAlreadyExists(Exception ex, WebRequest request) {
        ErrorMessage response = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),new Date(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorMessage> handleResourceNotFound(Exception ex, WebRequest request)  {
        ErrorMessage response = new ErrorMessage(HttpStatus.NOT_FOUND.value(),new Date(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException(AccessDeniedException ex, WebRequest request) {
        ErrorMessage response = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),new Date(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> handleAllException(Exception ex, WebRequest request) {
        ErrorMessage response = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),new Date(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}