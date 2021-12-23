package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY)
public class LoginRedirectException extends Exception {

    public LoginRedirectException(String msg) {
        super(msg);
    }

    public LoginRedirectException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public LoginRedirectException(Throwable thr) {
        super(thr);
    }
}
