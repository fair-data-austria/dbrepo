package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserNameExistsException extends Exception {

    public UserNameExistsException(String msg) {
        super(msg);
    }

    public UserNameExistsException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public UserNameExistsException(Throwable thr) {
        super(thr);
    }
}
