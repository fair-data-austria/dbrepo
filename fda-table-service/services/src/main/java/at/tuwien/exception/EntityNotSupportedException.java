package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class EntityNotSupportedException extends Exception {

    public EntityNotSupportedException(String msg) {
        super(msg);
    }

    public EntityNotSupportedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public EntityNotSupportedException(Throwable thr) {
        super(thr);
    }
}
