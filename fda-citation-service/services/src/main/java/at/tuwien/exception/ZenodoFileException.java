package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ZenodoFileException extends Exception {

    public ZenodoFileException(String msg) {
        super(msg);
    }

    public ZenodoFileException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoFileException(Throwable thr) {
        super(thr);
    }

}
