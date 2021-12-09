package at.tuwien.endpoints;

import at.tuwien.config.FdaConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationEndpoint {

    private final FdaConfig fdaConfig;

    @Autowired
    public AuthenticationEndpoint(FdaConfig fdaConfig) {
        this.fdaConfig = fdaConfig;
    }

    @GetMapping("/")
    public void authenticated(HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication.isAuthenticated()) {
            response.sendRedirect(fdaConfig.getBaseUrl());
        }
    }

}