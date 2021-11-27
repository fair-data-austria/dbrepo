package at.tuwien.config;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnleashConfig {

    @Value("${unleash.instance.id")
    private String instanceId;

    @Value("${unleash.api.url")
    private String apiUrl;

    @Bean
    public Unleash unleash() {
        final io.getunleash.util.UnleashConfig config = io.getunleash.util.UnleashConfig.builder()
                .appName("FDA DBRepo Table Service")
                .instanceId(instanceId)
                .unleashAPI(apiUrl)
                .build();
        return new DefaultUnleash(config);
    }

}
