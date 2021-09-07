package at.tuwien.config;

import com.google.common.io.Files;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;

@Configuration
public class ReadyConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {
        Files.touch(new File("/ready"));
    }

}
