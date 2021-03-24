package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import springfox.documentation.oas.annotations.EnableOpenApi;


@SpringBootApplication
@EnableOpenApi
@EntityScan(basePackages = {"at.tuwien.entity"})
public class FdaTableServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaTableServiceApplication.class, args);
    }

}
