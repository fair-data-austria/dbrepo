package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Container is still running")
public class ContainerStillRunningException extends Exception {

    public ContainerStillRunningException(String message) {
        super(message);
    }

    public ContainerStillRunningException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerStillRunningException(Throwable thr) {
        super(thr);
    }

}
