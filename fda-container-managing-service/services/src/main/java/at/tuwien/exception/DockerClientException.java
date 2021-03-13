package at.tuwien.exception;

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
