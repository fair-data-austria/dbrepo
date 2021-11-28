package at.tuwien.config;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;

@Log4j2
@Configuration
public class ReadyConfig {

    @Value("${fda.ready.path}")
    private String readyPath;

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {
        log.debug("Service ready");
        Files.touch(new File(readyPath));
    }

}
