package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Container not found")
public class ContainerNotFoundException extends ResponseStatusException {

    public ContainerNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ContainerNotFoundException(String message, Throwable thr) {
        super(HttpStatus.NOT_FOUND, message, thr);
    }
    
}
