package at.tuwien.exception;

public class ReflectAccessException extends Exception {

    public ReflectAccessException(String msg) {
        super(msg);
    }

    public ReflectAccessException(String msg, Throwable thr) {
        super(msg, thr);
    }

    public ReflectAccessException(Throwable thr) {
        super(thr);
    }
}
