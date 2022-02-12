package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TableNotFoundException extends Exception {

    public TableNotFoundException(String msg) {
        super(msg);
    }

    public TableNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public TableNotFoundException(Throwable thr) {
        super(thr);
    }

}
