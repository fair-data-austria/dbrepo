package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Container is still running")
public class ContainerStillRunningException extends Exception {

    public ContainerStillRunningException(String msg) {
        super(msg);
    }

    public ContainerStillRunningException(String msg, Throwable e) {
        super(msg, e);
    }

    public ContainerStillRunningException(Throwable e) {
        super(e);
    }

}
