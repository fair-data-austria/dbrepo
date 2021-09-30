package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class ZenodoUnavailableException extends Exception {

    public ZenodoUnavailableException(String msg) {
        super(msg);
    }

    public ZenodoUnavailableException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoUnavailableException(Throwable thr) {
        super(thr);
    }

}
