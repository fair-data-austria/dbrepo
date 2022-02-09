package at.tuwien.config;

import at.tuwien.seeder.Seeder;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class ReadyConfig {

    @Value("${fda.ready.path}")
    private String readyPath;

    private final Seeder seederImpl;
    private final Environment environment;

    @Autowired
    public ReadyConfig(Seeder seederImpl, Environment environment) {
        this.seederImpl = seederImpl;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {
        if (Arrays.asList(this.environment.getActiveProfiles()).contains("seeder")) {
            seederImpl.seed();
        }
        Files.touch(new File(readyPath));
    }

}
