package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ZenodoAuthenticationException extends Exception {

    public ZenodoAuthenticationException(String msg) {
        super(msg);
    }

    public ZenodoAuthenticationException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoAuthenticationException(Throwable thr) {
        super(thr);
    }

}
