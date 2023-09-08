package personal.dividend.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import personal.dividend.exception.general.AbstractGeneralException;
import personal.dividend.exception.response.ErrorResponse;
import personal.dividend.exception.significant.AbstractSignificantException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AbstractGeneralException.class)
    protected ResponseEntity<ErrorResponse> handleGeneralException(AbstractGeneralException e) {

        LOG.info("Exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));

    }

    @ExceptionHandler(AbstractSignificantException.class)
    public ResponseEntity<ErrorResponse> handleSignificantException
            (AbstractSignificantException e) {

        LOG.warn("Warning exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));

    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException
            (IOException e) {

        LOG.error("Error exception occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(HttpStatus.INTERNAL_SERVER_ERROR.value()));

    }

}
