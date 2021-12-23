package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Image already exists")
public class ImageAlreadyExistsException extends ResponseStatusException {

    public ImageAlreadyExistsException(String message) {
        super(HttpStatus.NOT_ACCEPTABLE, message);
    }

    public ImageAlreadyExistsException(String message, Throwable thr) {
        super(HttpStatus.NOT_ACCEPTABLE, message, thr);
    }

}
