package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TableMalformedException extends IOException {

    public TableMalformedException(String msg) {
        super(msg);
    }

    public TableMalformedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public TableMalformedException(Throwable thr) {
        super(thr);
    }

}
