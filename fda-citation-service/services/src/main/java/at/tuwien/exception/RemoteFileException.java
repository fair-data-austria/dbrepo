package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RemoteFileException extends Exception {

    public RemoteFileException(String msg) {
        super(msg);
    }

    public RemoteFileException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteFileException(Throwable thr) {
        super(thr);
    }

}
