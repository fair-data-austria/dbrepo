package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationEndpoint {

    @RequestMapping(value = {"/", "/index", "/logged-in"})
    public String home() {
        log.info("Sample SP Application - You are logged in!");
        return "logged-in";
    }

}