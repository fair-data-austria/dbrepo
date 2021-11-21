package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RemoteApiException extends Exception {

    public RemoteApiException(String msg) {
        super(msg);
    }

    public RemoteApiException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteApiException(Throwable thr) {
        super(thr);
    }

}
