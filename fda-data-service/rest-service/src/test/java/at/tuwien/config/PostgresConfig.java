package at.tuwien.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Properties;

@Configurable
public class PostgresConfig {

    @Bean
    public Properties postgresProperties() {
        final Properties properties = new Properties();
        properties.setProperty("POSTGRES_USER", "postgres");
        properties.setProperty("POSTGRES_PASSWORD", "postgres");
        return properties;
    }

    public static void clearDatabase(String containerHost, String databaseName, String tableName) throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://" + containerHost + "/" + databaseName,
                "postgres", "postgres");
        final Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + tableName + ";");
        connection.close();
    }

}
