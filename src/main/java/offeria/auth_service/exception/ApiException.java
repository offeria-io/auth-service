package offeria.auth_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom application exception with HTTP status.
 */
@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
