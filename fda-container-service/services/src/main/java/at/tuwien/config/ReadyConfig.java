package at.tuwien.config;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Log4j2
@Configuration
public class ReadyConfig {

    @Value("${fda.ready.path}")
    private String readyPath;

    private final Environment environment;

    @Autowired
    public ReadyConfig(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            log.info("Service is ready");
            Files.touch(new File(readyPath));
        }
    }

}
