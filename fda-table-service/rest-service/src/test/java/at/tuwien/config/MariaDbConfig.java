package at.tuwien.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Configurable
public class MariaDbConfig {

    @Bean
    public Properties mariaDbProperties() {
        final Properties properties = new Properties();
        properties.setProperty("MARIADB_USER", "mariadb");
        properties.setProperty("MARIADB_PASSWORD", "mariadb");
        return properties;
    }

    public static void clearDatabase(String containerHost, String databaseName, String tableName) throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:mariadb://" + containerHost + "/" + databaseName,
                "mariadb", "mariadb");
        final Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + tableName + ";");
        connection.close();
    }

}
