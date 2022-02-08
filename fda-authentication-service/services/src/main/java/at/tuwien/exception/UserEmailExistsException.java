package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class UserEmailExistsException extends Exception {

    public UserEmailExistsException(String msg) {
        super(msg);
    }

    public UserEmailExistsException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public UserEmailExistsException(Throwable thr) {
        super(thr);
    }
}
