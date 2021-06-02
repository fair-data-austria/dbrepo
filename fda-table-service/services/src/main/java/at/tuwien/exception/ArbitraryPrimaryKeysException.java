package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ArbitraryPrimaryKeysException extends Exception {

    public ArbitraryPrimaryKeysException(String msg) {
        super(msg);
    }

    public ArbitraryPrimaryKeysException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ArbitraryPrimaryKeysException(Throwable thr) {
        super(thr);
    }
}
