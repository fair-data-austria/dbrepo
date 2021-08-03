package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class QueryMalformedException extends Exception {

    public QueryMalformedException(String msg) {
        super(msg);
    }

    public QueryMalformedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public QueryMalformedException(Throwable thr) {
        super(thr);
    }

}
