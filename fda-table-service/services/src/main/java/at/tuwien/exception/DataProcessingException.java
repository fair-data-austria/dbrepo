package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataProcessingException extends Exception {

    public DataProcessingException(String msg) {
        super(msg);
    }

    public DataProcessingException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public DataProcessingException(Throwable thr) {
        super(thr);
    }

}
