package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ImageNotSupportedException extends Exception {

    public ImageNotSupportedException(String msg) {
        super(msg);
    }

    public ImageNotSupportedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ImageNotSupportedException(Throwable thr) {
        super(thr);
    }

}
