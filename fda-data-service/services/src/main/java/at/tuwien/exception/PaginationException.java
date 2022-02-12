package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PaginationException extends Exception {

    public PaginationException(String msg) {
        super(msg);
    }

    public PaginationException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public PaginationException(Throwable thr) {
        super(thr);
    }

}
