package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CreatorMissingException extends Exception {

    public CreatorMissingException(String msg) {
        super(msg);
    }

    public CreatorMissingException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public CreatorMissingException(Throwable thr) {
        super(thr);
    }

}
