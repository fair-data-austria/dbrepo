package at.tuwien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableJpaAuditing
@EnableOpenApi
@EnableElasticsearchRepositories(basePackages = {"at.tuwien.repository.elastic"})
@EnableJpaRepositories(basePackages = {"at.tuwien.repository.jpa"})
@EntityScan(basePackages = {"at.tuwien.entities"})
public class FdaContainerManagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdaContainerManagingApplication.class, args);
    }

}
