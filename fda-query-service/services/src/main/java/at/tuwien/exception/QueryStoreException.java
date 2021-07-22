package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class QueryStoreException  extends Exception {

    public QueryStoreException(String msg) {
        super(msg);
    }

    public QueryStoreException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public QueryStoreException(Throwable thr) { super(thr);
    }
}
