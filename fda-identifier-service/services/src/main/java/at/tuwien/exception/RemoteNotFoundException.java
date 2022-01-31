package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RemoteNotFoundException extends Exception {

    public RemoteNotFoundException(String msg) {
        super(msg);
    }

    public RemoteNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteNotFoundException(Throwable thr) {
        super(thr);
    }

}
