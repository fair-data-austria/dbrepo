package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.validation.constraints.NotNull;

@Configuration
public class QueryServiceConfig {

    @Getter
    @NotNull
    @Value("${fda.query.endpoint}")
    private String queryEndpoint;

    @Bean
    public RestTemplate queryTemplate() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://" + queryEndpoint))
                .build();
    }

}
