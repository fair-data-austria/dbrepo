package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class FdaContainerManagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaContainerManagingApplication.class, args);
    }

}
