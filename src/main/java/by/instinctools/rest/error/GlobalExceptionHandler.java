package by.instinctools.rest.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.bind.ValidationException;
import java.util.UUID;

/**
 * Exception handler component.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle exception when intended handler are not found.
     *
     * @param exception an exception
     * @return an error message
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity handleAnException(final Exception exception) {
        final UUID errorUUID = UUID.randomUUID();
        LOGGER.error("Error-ID: {} - {}", errorUUID, exception.getMessage(), exception);

        final String message = "An error occurred. Please contact support. Error-ID:" + errorUUID;
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

    /**
     * Handle {@link ValidationException}.
     *
     * @param exception an exception
     * @return an error message
     */
    @ExceptionHandler({ValidationException.class})
    @ResponseBody
    public ResponseEntity handleAnValidationException(final ValidationException exception) {

        final String message = exception.getMessage();

        LOGGER.warn("Caught ValidationException: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
}
