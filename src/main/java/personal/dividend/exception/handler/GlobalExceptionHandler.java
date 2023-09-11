package personal.dividend.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import personal.dividend.exception.general.AbstractGeneralException;
import personal.dividend.exception.response.ErrorResponse;
import personal.dividend.exception.serious.AbstractSeriousException;
import personal.dividend.exception.significant.AbstractSignificantException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AbstractGeneralException.class)
    protected ResponseEntity<ErrorResponse> handleGeneralException(AbstractGeneralException e) {

        log.info("Exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(errorResponse.getStatusCode()));

    }

    @ExceptionHandler(AbstractSignificantException.class)
    public ResponseEntity<ErrorResponse> handleSignificantException
            (AbstractSignificantException e) {

        log.warn("Warning exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(errorResponse.getStatusCode()));

    }

    @ExceptionHandler(AbstractSeriousException.class)
    public ResponseEntity<ErrorResponse> handleAbstractSeriousException
            (AbstractSeriousException e) {

        log.error("Error exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(errorResponse.getStatusCode()));

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException
            (EntityNotFoundException e) {

        log.warn("Warning exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(errorResponse.getStatusCode()));

    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException
            (IOException e) {

        log.error("Error exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(errorResponse.getStatusCode()));

    }

}
