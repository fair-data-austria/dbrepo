package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class DatabaseNotFoundException extends IOException {

    public DatabaseNotFoundException(String msg) {
        super(msg);
    }

    public DatabaseNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public DatabaseNotFoundException(Throwable thr) {
        super(thr);
    }

}
