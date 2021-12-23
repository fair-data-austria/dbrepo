package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Container is still running")
public class ContainerStillRunningException extends ResponseStatusException {

    public ContainerStillRunningException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public ContainerStillRunningException(String message, Throwable thr) {
        super(HttpStatus.CONFLICT, message, thr);
    }

}
