package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
public class RemoteDatabaseException extends Exception {

    public RemoteDatabaseException(String msg) {
        super(msg);
    }

    public RemoteDatabaseException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteDatabaseException(Throwable thr) {
        super(thr);
    }

}
