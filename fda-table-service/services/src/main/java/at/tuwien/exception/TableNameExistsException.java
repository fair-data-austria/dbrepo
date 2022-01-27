package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class TableNameExistsException extends Exception {

    public TableNameExistsException(String msg) {
        super(msg);
    }

    public TableNameExistsException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public TableNameExistsException(Throwable thr) {
        super(thr);
    }

}
