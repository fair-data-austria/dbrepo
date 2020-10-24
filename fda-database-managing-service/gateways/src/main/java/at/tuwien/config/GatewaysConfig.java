package at.tuwien.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewaysConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder getWebclientBuilder() {
        return WebClient.builder();
    }
}
