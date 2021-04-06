package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "at.tuwien")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"at.tuwien.repository"})
@EnableOpenApi
public class FdaQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaQueryServiceApplication.class, args);
    }

}
