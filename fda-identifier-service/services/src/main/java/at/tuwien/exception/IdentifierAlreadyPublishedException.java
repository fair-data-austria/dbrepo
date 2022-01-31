package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
public class IdentifierAlreadyPublishedException extends Exception {

    public IdentifierAlreadyPublishedException(String msg) {
        super(msg);
    }

    public IdentifierAlreadyPublishedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public IdentifierAlreadyPublishedException(Throwable thr) {
        super(thr);
    }

}
