package at.tuwien.endpoints;

import org.springframework.web.bind.annotation.*;

@RestController("/api/auth")
public class AuthenticationEndpoint {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

}