package at.tuwien.handlers;

import at.tuwien.api.error.ApiErrorDto;
import at.tuwien.exception.*;
import net.sf.jsqlparser.JSQLParserException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler({DatabaseNotFoundException.class})
    public ResponseEntity<Object> handle(DatabaseNotFoundException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.database.notfound")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({FileStorageException.class})
    public ResponseEntity<Object> handle(FileStorageException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.file.store")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({ImageNotSupportedException.class})
    public ResponseEntity<Object> handle(ImageNotSupportedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.CONFLICT)
                .message(e.getLocalizedMessage())
                .code("error.database.image")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({PaginationException.class})
    public ResponseEntity<Object> handle(PaginationException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .code("error.query.pagination")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({QueryMalformedException.class})
    public ResponseEntity<Object> handle(QueryMalformedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .code("error.query.malformed")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({QueryNotFoundException.class})
    public ResponseEntity<Object> handle(QueryNotFoundException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.query.notfound")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({QueryStoreException.class})
    public ResponseEntity<Object> handle(QueryStoreException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.CONFLICT)
                .message(e.getLocalizedMessage())
                .code("error.querystore.exists")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({TableMalformedException.class})
    public ResponseEntity<Object> handle(TableMalformedException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .code("error.table.malformed")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({TableNotFoundException.class})
    public ResponseEntity<Object> handle(TableNotFoundException e, WebRequest request) {
        final ApiErrorDto response = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .code("error.table.notfound")
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

}
