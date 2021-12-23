package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class RemoteUnavailableException extends Exception {

    public RemoteUnavailableException(String msg) {
        super(msg);
    }

    public RemoteUnavailableException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteUnavailableException(Throwable thr) {
        super(thr);
    }

}
