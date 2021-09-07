package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class AmqpException extends Exception {

    public AmqpException(String msg) {
        super(msg);
    }

    public AmqpException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public AmqpException(Throwable thr) {
        super(thr);
    }

}
