package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Persistence error")
public class PersistenceException extends Exception {

   public PersistenceException(String msg) {
       super(msg);
   }

   public PersistenceException(String msg, Throwable thr) {
       super(msg, thr);
   }

    public PersistenceException(Throwable thr) {
        super(thr);
    }

}
