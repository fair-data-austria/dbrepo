package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ZenodoNotFoundException extends Exception {

    public ZenodoNotFoundException(String msg) {
        super(msg);
    }

    public ZenodoNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoNotFoundException(Throwable thr) {
        super(thr);
    }

}
