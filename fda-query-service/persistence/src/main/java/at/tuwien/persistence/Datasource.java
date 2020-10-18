package at.tuwien.persistence;

import at.tuwien.dto.QueryDatabaseDTO;
import at.tuwien.pojo.DatabaseConnectionDataPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class Datasource {

    private DataSource postgresDataSource;


    public ResultSet executeQuery(QueryDatabaseDTO dto, DatabaseConnectionDataPOJO pojo) {
        configureDatasource(pojo);
        Statement stmt = null;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = postgresDataSource.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(dto.getQuery());
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

        return rs;
    }

    public void configureDatasource(DatabaseConnectionDataPOJO pojo) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + pojo.getIpAddress() + "/" + pojo.getDbName());
        //should be entered from user!
        dataSource.setUsername("postgres");
        dataSource.setPassword("mysecretpassword");
        postgresDataSource = dataSource;

    }
}
