package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class StreamException extends Exception {

    public StreamException(String msg) {
        super(msg);
    }

    public StreamException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public StreamException(Throwable thr) {
        super(thr);
    }

}
