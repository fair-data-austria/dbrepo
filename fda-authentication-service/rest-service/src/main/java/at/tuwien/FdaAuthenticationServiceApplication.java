package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableWebMvc
@EnableOpenApi
@SpringBootApplication
public class FdaAuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaAuthenticationServiceApplication.class, args);
    }

}
