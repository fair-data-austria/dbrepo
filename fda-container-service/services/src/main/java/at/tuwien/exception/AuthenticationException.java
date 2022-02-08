package at.tuwien.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Persistence error")
public class AuthenticationException extends Exception {

   public AuthenticationException(String msg) {
       super(msg);
   }

   public AuthenticationException(String msg, Throwable thr) {
       super(msg, thr);
   }

    public AuthenticationException(Throwable thr) {
        super(thr);
    }

}
