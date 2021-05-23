package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class FileStorageException extends RuntimeException {

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
