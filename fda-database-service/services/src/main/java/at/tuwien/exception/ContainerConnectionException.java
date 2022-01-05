package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY, reason = "Container connection failed")
public class ContainerConnectionException extends Exception {

    public ContainerConnectionException(String message) {
        super(message);
    }

    public ContainerConnectionException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerConnectionException(Throwable thr) {
        super(thr);
    }

}
