package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class QueryNotFoundException extends Exception {

    public QueryNotFoundException(String msg) {
        super(msg);
    }

    public QueryNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public QueryNotFoundException(Throwable thr) {
        super(thr);
    }

}
