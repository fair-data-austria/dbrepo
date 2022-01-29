package at.tuwien.config;

import at.tuwien.entities.database.table.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Configuration
public class MariaDbConfig {

    public static void clearQueryStore(Table table) throws SQLException {
        final String jdbc = "jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName();
        log.trace("connect to database {}", jdbc);
        final Connection connection = DriverManager.getConnection(jdbc, "root", "mariadb");
        final Statement statement = connection.createStatement();
        statement.execute("DROP TABLE IF EXISTS qs_queries;");
        statement.execute("DROP TABLE IF EXISTS qs_seq;");
        connection.close();
    }

    public static List<List<String>> select(Table table, Integer rowCount) throws SQLException {
        final String jdbc = "jdbc:mariadb://" + table.getDatabase().getContainer().getInternalName() + "/" + table.getDatabase().getInternalName();
        log.trace("connect to database {}", jdbc);
        final List<List<String>> rows = new LinkedList<>();
        final Connection connection = DriverManager.getConnection(jdbc, "root", "mariadb");
        final Statement statement = connection.createStatement();
        final StringBuilder query = new StringBuilder("SELECT ");
        final int[] idx = new int[]{0};
        table.getColumns()
                .forEach(column -> query.append(idx[0]++ > 0 ? "," : "")
                        .append("`")
                        .append(column.getInternalName())
                        .append("`"));
        query.append(" FROM `")
                .append(table.getInternalName())
                .append("` LIMIT ")
                .append(rowCount)
                .append(";");
        log.trace("raw select query [" + query + "]");
        final ResultSet result = statement.executeQuery(query.toString());
        while (result.next()) {
            final List<String> row = new LinkedList<>();
            for (int i = 0; i < table.getColumns().size(); i++) {
                row.add(result.getString(table.getColumns()
                        .get(i)
                        .getInternalName()));
            }
            rows.add(row);
        }
        connection.close();
        return rows;
    }
}
