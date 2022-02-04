package at.tuwien.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class GatewayConfig {

    @Value("${fda.auth.endpoint}")
    private String authEndpoint;

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate =  new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(authEndpoint));
        return restTemplate;
    }

}
