package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ConstructorNotFoundException extends Exception {

    public ConstructorNotFoundException(String msg) {
        super(msg);
    }

    public ConstructorNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ConstructorNotFoundException(Throwable thr) {
        super(thr);
    }
}
