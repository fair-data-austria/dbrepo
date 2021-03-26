package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Database not found")
public class DatabaseNotFoundException extends Exception {

    public DatabaseNotFoundException(String message) {
        super(message);
    }

    public DatabaseNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }

    public DatabaseNotFoundException(Throwable thr) {
        super(thr);
    }

}
