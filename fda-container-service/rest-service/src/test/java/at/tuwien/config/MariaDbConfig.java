package at.tuwien.config;

import at.tuwien.entities.database.table.Table;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.sql.*;
import java.util.Properties;

@Log4j2
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
        final String jdbc = "jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName();
        log.trace("connect to database {}", jdbc);
        final Connection connection = DriverManager.getConnection(jdbc, "mariadb", "mariadb");
        final Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + table.getInternalName() + ";");
        connection.close();
    }

    public static boolean contains(Table table, String column, Object expected) throws SQLException {
        final String jdbc = "jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName();
        log.trace("connect to database {}", jdbc);
        final Connection connection = DriverManager.getConnection(jdbc, "mariadb", "mariadb");
        final Statement statement = connection.createStatement();
        final ResultSet result = statement.executeQuery("SELECT `" + column + "` FROM " + table.getInternalName() +
                " WHERE `" + column + "` = " + expected.toString() + ";");
        connection.close();
        return result.next();
    }

}
