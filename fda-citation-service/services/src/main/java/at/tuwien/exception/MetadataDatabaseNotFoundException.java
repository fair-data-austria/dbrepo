package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class MetadataDatabaseNotFoundException extends Exception {

    public MetadataDatabaseNotFoundException(String msg) {
        super(msg);
    }

    public MetadataDatabaseNotFoundException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public MetadataDatabaseNotFoundException(Throwable thr) {
        super(thr);
    }

}
