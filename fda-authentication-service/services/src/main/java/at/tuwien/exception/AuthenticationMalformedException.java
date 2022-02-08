package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AuthenticationMalformedException extends Exception {

    public AuthenticationMalformedException(String msg) {
        super(msg);
    }

    public AuthenticationMalformedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public AuthenticationMalformedException(Throwable thr) {
        super(thr);
    }
}
