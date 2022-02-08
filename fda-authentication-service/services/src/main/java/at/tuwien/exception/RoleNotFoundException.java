package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends Exception {

    public RoleNotFoundException(String msg) {
        super(msg);
    }

    public RoleNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RoleNotFoundException(Throwable thr) {
        super(thr);
    }
}
