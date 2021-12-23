package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TableServiceException extends Exception {

    public TableServiceException(String msg) {
        super(msg);
    }

    public TableServiceException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public TableServiceException(Throwable thr) {
        super(thr);
    }

}
