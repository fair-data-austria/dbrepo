package at.tuwien.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@Configuration
public class ZenodoConfig {

    @Getter
    @Value("${zenodo.endpoint}")
    private String zenodoEndpoint;

    @Getter
    @Value("${zenodo.api_key}")
    private String zenodoApiKey;

    @Bean
    public RestTemplate zenodo() {
        DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();
        defaultUriTemplateHandler.setBaseUrl(zenodoEndpoint);
        return new RestTemplateBuilder()
                .uriTemplateHandler(defaultUriTemplateHandler)
                .build();
    }

}
