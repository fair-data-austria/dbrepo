package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DatabaseMalformedException extends IOException {

    public DatabaseMalformedException(String msg) {
        super(msg);
    }

    public DatabaseMalformedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public DatabaseMalformedException(Throwable thr) {
        super(thr);
    }

}
