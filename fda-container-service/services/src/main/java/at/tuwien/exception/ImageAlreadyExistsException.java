package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Image already exists")
public class ImageAlreadyExistsException extends Exception {

    public ImageAlreadyExistsException(String message) {
        super(message);
    }

    public ImageAlreadyExistsException(String message, Throwable thr) {
        super(message, thr);
    }

    public ImageAlreadyExistsException(Throwable thr) {
        super(thr);
    }

}
