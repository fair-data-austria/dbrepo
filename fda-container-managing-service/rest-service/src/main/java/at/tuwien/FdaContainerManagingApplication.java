package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"at.tuwien.repository"})
@EntityScan(basePackages = {"at.tuwien.entities"})
@EnableSwagger2
public class FdaContainerManagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaContainerManagingApplication.class, args);
    }

}
