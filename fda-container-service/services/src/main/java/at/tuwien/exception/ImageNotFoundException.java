package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Image not found")
public class ImageNotFoundException extends ResponseStatusException {

    public ImageNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ImageNotFoundException(String message, Throwable thr) {
        super(HttpStatus.NOT_FOUND, message, thr);
    }

}
