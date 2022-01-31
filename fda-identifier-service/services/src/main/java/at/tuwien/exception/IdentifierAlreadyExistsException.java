package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class IdentifierAlreadyExistsException extends Exception {

    public IdentifierAlreadyExistsException(String msg) {
        super(msg);
    }

    public IdentifierAlreadyExistsException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public IdentifierAlreadyExistsException(Throwable thr) {
        super(thr);
    }

}
