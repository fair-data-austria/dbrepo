package at.tuwien.handlers;

import at.tuwien.api.error.ApiErrorDto;
import at.tuwien.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AmqpException.class})
    public ResponseEntity<Object> handle(AmqpException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.amqp.exchange")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({BrokerMalformedException.class})
    public ResponseEntity<Object> handle(BrokerMalformedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.amqp.exchange")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({ContainerConnectionException.class})
    public ResponseEntity<Object> handle(ContainerConnectionException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.container.connection")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({ContainerNotFoundException.class})
    public ResponseEntity<Object> handle(ContainerNotFoundException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.container.notfound")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({DatabaseConnectionException.class})
    public ResponseEntity<Object> handle(DatabaseConnectionException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .message(e.getLocalizedMessage())
                .code("error.database.connection")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({DatabaseMalformedException.class})
    public ResponseEntity<Object> handle(DatabaseMalformedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .code("error.database.malformed")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({DatabaseNotFoundException.class})
    public ResponseEntity<Object> handle(DatabaseNotFoundException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.database.notfound")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({ImageNotSupportedException.class})
    public ResponseEntity<Object> handle(ImageNotSupportedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_ACCEPTABLE)
                .message(e.getLocalizedMessage())
                .code("error.image.notsupported")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

}