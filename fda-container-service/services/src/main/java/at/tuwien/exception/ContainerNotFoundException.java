package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Container not found")
public class ContainerNotFoundException extends Exception {

    public ContainerNotFoundException(String msg) {
        super(msg);
    }

    public ContainerNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }

    public ContainerNotFoundException(Throwable e) {
        super(e);
    }

}