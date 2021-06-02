package at.tuwien.exception;

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
