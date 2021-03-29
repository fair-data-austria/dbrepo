package at.tuwien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
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
    public Docket queryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("query-api")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("FDA-Query-Service API",
                "Service API for query service",
                "1.0",
                null,
                new Contact("Gökhan Dasdemir", "http://tuwien.at", "goekhan.dasdemir@tuwien.ac.at"),
                "API license",
                null,
                Collections.emptyList());


    }

}

