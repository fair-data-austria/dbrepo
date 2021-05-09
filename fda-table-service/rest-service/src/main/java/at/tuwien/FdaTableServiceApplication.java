package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;


@SpringBootApplication
@EnableJpaAuditing
@EnableOpenApi
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"at.tuwien.repository"})
@EntityScan(basePackages = {"at.tuwien.entities"})
public class FdaTableServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaTableServiceApplication.class, args);
    }

}
