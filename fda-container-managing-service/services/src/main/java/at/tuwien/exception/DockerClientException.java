package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DockerClientException extends Exception {

    public DockerClientException(String message) {
        super(message);
    }

    public DockerClientException(String message, Throwable thr) {
        super(message, thr);
    }

    public DockerClientException(Throwable thr) {
        super(thr);
    }

}
