package at.tuwien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.HashSet;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket databaseApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("database-api")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.ant("/api/database.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("FDA-Container-Managing API",
                "Service that can manage Docker containers",
                "1.0",
                null,
                new Contact("Martin Weise", "https://informatics.tuwien.ac.at/people/martin-weise", "martin.weise@tuwien.ac.at"),
                "API license",
                null,
                Collections.emptyList());


    }

}
