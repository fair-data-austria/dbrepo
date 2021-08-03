package at.tuwien.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@Configurable
public class PostgresConfig {

    @Bean
    public Properties postgresProperties() {
        final Properties properties = new Properties();
        properties.setProperty("POSTGRES_USER", "postgres");
        properties.setProperty("POSTGRES_PASSWORD", "postgres");
        properties.setProperty("POSTGRES_DB", "u01");
        return properties;
    }

}
