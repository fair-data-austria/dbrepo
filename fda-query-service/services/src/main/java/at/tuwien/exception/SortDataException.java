package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SortDataException extends Exception {

    public SortDataException(String msg) {
        super(msg);
    }

    public SortDataException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public SortDataException(Throwable thr) {
        super(thr);
    }

}
