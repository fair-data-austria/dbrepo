package at.tuwien.endpoints;

import at.tuwien.config.FdaProperties;
import at.tuwien.exception.LoginRedirectException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/")
public class RedirectEndpoint {

    private final FdaProperties fdaProperties;

    @Autowired
    public RedirectEndpoint(FdaProperties fdaProperties) {
        this.fdaProperties = fdaProperties;
    }

    @RequestMapping("/")
    public void index(HttpServletResponse response) throws LoginRedirectException {
        log.debug("logged in");
        try {
            response.sendRedirect(fdaProperties.getLoginSuccessUrl());
        } catch (IOException e) {
            throw new LoginRedirectException("Sending redirect failed", e);
        }
    }

}