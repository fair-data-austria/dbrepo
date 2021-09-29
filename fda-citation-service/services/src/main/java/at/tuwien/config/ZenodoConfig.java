package at.tuwien.config;

import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.utils.ApiTemplateInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    public String getApiKey() throws ZenodoAuthenticationException {
        if (apiKey == null || apiKey.isEmpty()) {
            log.debug("api key is {}", apiKey);
            throw new ZenodoAuthenticationException("Did not find a valid Zenodo API key in environment variable ZENODO_API_KEY");
        }
        return apiKey;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
    }

    @Bean
    public RestTemplate apiTemplate() {
        final UriBuilderFactory factory = new DefaultUriBuilderFactory(zenodoEndpoint);
        return new RestTemplateBuilder()
                .uriTemplateHandler(factory)
                .build();
    }

}