package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class RemoteAuthenticationException extends Exception {

    public RemoteAuthenticationException(String msg) {
        super(msg);
    }

    public RemoteAuthenticationException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteAuthenticationException(Throwable thr) {
        super(thr);
    }

}
