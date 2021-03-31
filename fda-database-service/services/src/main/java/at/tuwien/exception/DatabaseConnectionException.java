package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
public class DatabaseConnectionException extends IOException {

    public DatabaseConnectionException(String msg) {
        super(msg);
    }

    public DatabaseConnectionException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public DatabaseConnectionException(Throwable thr) {
        super(thr);
    }

}
