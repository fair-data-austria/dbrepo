package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ContainerNotRunningException extends Exception {

    public ContainerNotRunningException(String message) {
        super(message);
    }

    public ContainerNotRunningException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerNotRunningException(Throwable thr) {
        super(thr);
    }

}
