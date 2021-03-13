package at.tuwien.exception;

public class ContainerNotFoundException extends Exception {

    public ContainerNotFoundException(String message) {
        super(message);
    }

    public ContainerNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }

    public ContainerNotFoundException(Throwable thr) {
        super(thr);
    }

}
