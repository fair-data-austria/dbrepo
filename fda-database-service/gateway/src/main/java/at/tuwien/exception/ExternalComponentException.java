package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "external service failed")
public class ExternalComponentException extends Exception {

    public ExternalComponentException(String message) {
        super(message);
    }

    public ExternalComponentException(String message, Throwable thr) {
        super(message, thr);
    }

    public ExternalComponentException(Throwable thr) {
        super(thr);
    }

}
