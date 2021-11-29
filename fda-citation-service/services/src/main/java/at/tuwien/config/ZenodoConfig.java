package at.tuwien.config;

import at.tuwien.exception.RemoteAuthenticationException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.validation.constraints.NotNull;

@Log4j2
@Configuration
public class ZenodoConfig {

    @Getter
    @NotNull
    @Value("${zenodo.endpoint}")
    private String zenodoEndpoint;

    @NotNull
    @Value("${zenodo.api_key}")
    private String apiKey;

    public String getApiKey() throws RemoteAuthenticationException {
        if (apiKey == null || apiKey.isEmpty()) {
            log.debug("api key is {}", apiKey);
            throw new RemoteAuthenticationException("Did not find a valid Zenodo API key in environment variable ZENODO_API_KEY");
        }
        return apiKey;
    }

    @Bean
    public RestTemplate zenodoTemplate() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(zenodoEndpoint))
                .build();
    }

}
