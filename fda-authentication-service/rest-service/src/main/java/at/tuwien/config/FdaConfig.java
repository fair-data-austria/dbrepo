package at.tuwien.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Getter
@Configuration
public class FdaConfig {

    @Value("${fda.base-url}")
    private String baseUrl;

}
