package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class ZenodoFileTooLargeException extends Exception {

    public ZenodoFileTooLargeException(String msg) {
        super(msg);
    }

    public ZenodoFileTooLargeException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ZenodoFileTooLargeException(Throwable thr) {
        super(thr);
    }

}
