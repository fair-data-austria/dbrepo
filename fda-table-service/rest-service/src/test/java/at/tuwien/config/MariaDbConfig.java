package at.tuwien.config;

import at.tuwien.entities.database.table.Table;
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

    public static void clearDatabase(Table table) throws SQLException {
        final Connection connection = DriverManager.getConnection("jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName(),
                "mariadb", "mariadb");
        final Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + table.getInternalName() + ";");
        connection.close();
    }

}