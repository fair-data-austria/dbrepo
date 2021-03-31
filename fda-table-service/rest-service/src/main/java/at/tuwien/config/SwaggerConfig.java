package at.tuwien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Bean
    public Docket tableApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("table-api")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfo("FDA-Table-Service API",
                "Service that can manage Tables in Databases",
                "1.0",
                null,
                new Contact("Ao.Univ.Prof. Andreas Rauber", "http://www.ifs.tuwien.ac.at/~andi/", "rauber@ifs.tuwien.ac.at"),
                "API license",
                null,
                Collections.emptyList());


    }

}
