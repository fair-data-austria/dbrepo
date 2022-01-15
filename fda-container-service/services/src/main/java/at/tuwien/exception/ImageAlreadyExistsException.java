package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Image already exists")
public class ImageAlreadyExistsException extends Exception {

    public ImageAlreadyExistsException(String msg) {
        super(msg);
    }

    public ImageAlreadyExistsException(String msg, Throwable e) {
        super(msg, e);
    }

    public ImageAlreadyExistsException(Throwable e) {
        super(e);
    }

}