package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class RemoteFileTooLargeException extends Exception {

    public RemoteFileTooLargeException(String msg) {
        super(msg);
    }

    public RemoteFileTooLargeException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public RemoteFileTooLargeException(Throwable thr) {
        super(thr);
    }

}
