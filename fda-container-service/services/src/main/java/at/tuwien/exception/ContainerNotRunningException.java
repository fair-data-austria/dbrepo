package at.tuwien.exception;

public class ContainerNotRunningException extends Exception {

    public ContainerNotRunningException(String message) {
        super(message);
    }

    public ContainerNotRunningException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerNotRunningException(Throwable thr) {
        super(thr);
    }

}
