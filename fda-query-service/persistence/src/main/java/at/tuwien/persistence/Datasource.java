package at.tuwien.persistence;

import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.dto.ExecuteInternalQueryDTO;
import at.tuwien.pojo.DatabaseContainer;
import at.tuwien.util.ResultUtil;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class Datasource {

    private DataSource postgresDataSource;


    public List<Map<String, Object>>executeQuery(ExecuteInternalQueryDTO dto, DatabaseContainer databaseContainer) {
        configureDatasource(databaseContainer);
        List<Map<String, Object>> resultListOfMaps = null;
        Statement stmt = null;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = postgresDataSource.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(dto.getQuery());
            resultListOfMaps = ResultUtil.resultSetToListOfMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return resultListOfMaps;
    }

    public boolean executeStatement(ExecuteStatementDTO dto, DatabaseContainer databaseContainer) {
        configureDatasource(databaseContainer);
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = postgresDataSource.getConnection();
            stmt = connection.createStatement();
            stmt.execute(dto.getStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void configureDatasource(DatabaseContainer databaseContainer) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + databaseContainer.getIpAddress() + "/" + databaseContainer.getDbName());
        //should be entered from user!
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        postgresDataSource = dataSource;

    }
}
