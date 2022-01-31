package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class IdentifierPublishingNotAllowedException extends Exception {

    public IdentifierPublishingNotAllowedException(String msg) {
        super(msg);
    }

    public IdentifierPublishingNotAllowedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public IdentifierPublishingNotAllowedException(Throwable thr) {
        super(thr);
    }

}
