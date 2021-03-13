package at.tuwien.exception;

public class ImageNotFoundException extends Exception {

    public ImageNotFoundException(String message) {
        super(message);
    }

    public ImageNotFoundException(String message, Throwable thr) {
        super(message, thr);
    }

    public ImageNotFoundException(Throwable thr) {
        super(thr);
    }

}
