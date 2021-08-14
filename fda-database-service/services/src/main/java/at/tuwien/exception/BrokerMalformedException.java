package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BrokerMalformedException extends IOException {

    public BrokerMalformedException(String msg) {
        super(msg);
    }

    public BrokerMalformedException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public BrokerMalformedException(Throwable thr) {
        super(thr);
    }

}
