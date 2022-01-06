package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class IdentifierNotFoundException extends Exception {

    public IdentifierNotFoundException(String msg) {
        super(msg);
    }

    public IdentifierNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public IdentifierNotFoundException(Throwable thr) {
        super(thr);
    }

}
