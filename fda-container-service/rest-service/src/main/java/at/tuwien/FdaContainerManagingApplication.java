package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableJpaAuditing
@EnableOpenApi
@EnableJpaRepositories(basePackages = {"at.tuwien.repository"})
@EntityScan(basePackages = {"at.tuwien.entity"})
public class FdaContainerManagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaContainerManagingApplication.class, args);
    }

}
