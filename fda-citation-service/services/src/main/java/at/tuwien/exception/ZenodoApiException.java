package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ZenodoApiException extends Exception {

    public ZenodoApiException(String msg) {
        super(msg);
    }

    public ZenodoApiException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoApiException(Throwable thr) {
        super(thr);
    }

}
