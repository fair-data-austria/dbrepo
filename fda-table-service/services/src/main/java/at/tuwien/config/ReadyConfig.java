package at.tuwien.config;

import com.google.common.io.Files;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class ReadyConfig {

    @PostConstruct
    public void init() throws IOException {
        Files.touch(new File("/ready"));
    }

}
