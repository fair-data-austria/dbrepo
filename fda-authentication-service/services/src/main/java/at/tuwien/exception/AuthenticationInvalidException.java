package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AuthenticationInvalidException extends Exception {

    public AuthenticationInvalidException(String msg) {
        super(msg);
    }

    public AuthenticationInvalidException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public AuthenticationInvalidException(Throwable thr) {
        super(thr);
    }
}
