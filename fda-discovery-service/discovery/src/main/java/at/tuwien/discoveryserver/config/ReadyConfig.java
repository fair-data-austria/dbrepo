package at.tuwien.discoveryserver.config;

import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;

@Configuration
public class ReadyConfig {

    @Value("${fda.ready.path}")
    private String readyPath;

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {
        Files.touch(new File(readyPath));
    }

}
