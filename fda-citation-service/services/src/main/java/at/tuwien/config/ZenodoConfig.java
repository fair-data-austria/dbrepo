package at.tuwien.config;

import at.tuwien.utils.ZenodoTemplateInterceptor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
public class ZenodoConfig {

    @Getter
    @NotNull
    @Value("${zenodo.endpoint}")
    private String zenodoEndpoint;

    @Getter
    @NotNull
    @Value("${zenodo.api_key}")
    private String zenodoApiKey;

    @Bean
    public RestTemplate zenodo() {
        DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();
        defaultUriTemplateHandler.setBaseUrl(zenodoEndpoint);
        final RestTemplate template = new RestTemplateBuilder()
                .uriTemplateHandler(defaultUriTemplateHandler)
                .build();
        template.setInterceptors(List.of(new ZenodoTemplateInterceptor()));
        return template;
    }

}
