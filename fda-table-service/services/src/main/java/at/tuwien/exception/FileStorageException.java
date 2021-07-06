package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FileStorageException extends Exception {

    public FileStorageException(String msg) {
        super(msg);
    }

    public FileStorageException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public FileStorageException(Throwable thr) {
        super(thr);
    }
}
