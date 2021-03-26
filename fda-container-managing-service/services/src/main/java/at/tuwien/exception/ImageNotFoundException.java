package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Image not found")
public class ImageNotFoundException extends Exception {

    public ImageNotFoundException(String message) {
        super(message);
    }

    public ImageNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }

    public ImageNotFoundException(Throwable thr) {
        super(thr);
    }

}
