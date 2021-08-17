package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaAuditing
@EnableOpenApi
@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = {"at.tuwien.repository.jpa"})
@EnableElasticsearchRepositories(basePackages = {"at.tuwien.repository.elasticsearch"})
@EntityScan(basePackages = {"at.tuwien.entities"})
public class FdaDatabaseManagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaDatabaseManagingApplication.class, args);
    }

}
