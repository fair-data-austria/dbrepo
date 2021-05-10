package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Docker failed")
public class DockerClientException extends ResponseStatusException {

    public DockerClientException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public DockerClientException(String message, Throwable thr) {
        super(HttpStatus.BAD_REQUEST, message, thr);
    }

}
