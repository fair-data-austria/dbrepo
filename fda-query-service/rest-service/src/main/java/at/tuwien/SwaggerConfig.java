package at.tuwien;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket swaggerConfiguration() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/query/*"))
                .apis(RequestHandlerSelectors.basePackage("at.tuwien.controller"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("FDA-Query-Service API",
                "Service API for query at.tuwien.service",
                "1.0",
                null,
                new Contact("Gökhan Dasdemir", "http://tuwien.at", "goekhan.dasdemir@tuwien.ac.at"),
                "API license",
                null,
                Collections.emptyList());


    }

}

