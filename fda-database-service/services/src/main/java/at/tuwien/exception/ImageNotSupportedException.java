package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "image not supported")
public class ImageNotSupportedException extends Exception {

    public ImageNotSupportedException(String message) {
        super(message);
    }

    public ImageNotSupportedException(String message, Throwable thr) {
        super(message, thr);
    }

    public ImageNotSupportedException(Throwable thr) {
        super(thr);
    }

}
