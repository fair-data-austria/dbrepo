package at.tuwien.config;

import at.tuwien.entities.database.table.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Configuration
public class MariaDbConfig {

    public static void clearQueryStore(Table table) throws SQLException {
        final String jdbc = "jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName();
        log.trace("connect to database {}", jdbc);
        final Connection connection = DriverManager.getConnection(jdbc, "mariadb", "mariadb");
        final Statement statement = connection.createStatement();
        statement.execute("DROP TABLE IF EXISTS qs_queries;");
        statement.execute("DROP TABLE IF EXISTS qs_seq;");
        connection.close();
    }
}
