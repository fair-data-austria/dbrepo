package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Container name exists")
public class ContainerAlreadyExistsException extends Exception {

    public ContainerAlreadyExistsException(String message) {
        super(message);
    }

    public ContainerAlreadyExistsException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerAlreadyExistsException(Throwable thr) {
        super(thr);
    }

}
