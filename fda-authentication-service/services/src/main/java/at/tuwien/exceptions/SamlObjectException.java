package at.tuwien.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SamlObjectException extends Exception {

    public SamlObjectException(String msg) {
        super(msg);
    }

    public SamlObjectException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public SamlObjectException(Throwable thr) {
        super(thr);
    }
}
