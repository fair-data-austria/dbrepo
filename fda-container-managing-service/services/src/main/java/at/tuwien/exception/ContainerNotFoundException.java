package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ContainerNotFoundException extends Exception {

    public ContainerNotFoundException(String message) {
        super(message);
    }

    public ContainerNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerNotFoundException(Throwable thr) {
        super(thr);
    }

}
