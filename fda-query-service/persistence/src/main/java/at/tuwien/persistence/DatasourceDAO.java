package at.tuwien.persistence;

import at.tuwien.config.SpringJdbcPostgresConfig;
import at.tuwien.dto.QueryDatabaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class DatasourceDAO {
    @Autowired
    private DataSource postgresDataSource;


    public ResultSet executeQuery(String query) {
        configureDatasource();
        Statement stmt = null;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = postgresDataSource.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (stmt != null) {
//                    stmt.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        }
        return rs;
    }

    public void configureDatasource() {
/*        ((DriverManagerDataSource)postgresDataSource).setUrl("jdbc:postgresql://172.17.0.3/testC2");
        ((DriverManagerDataSource)postgresDataSource).setUsername("postgres");
        ((DriverManagerDataSource)postgresDataSource).setPassword("mysecretpassword");*/

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://172.17.0.3/testD");
        dataSource.setUsername("postgres");
        dataSource.setPassword("mysecretpassword");
        postgresDataSource = dataSource;

    }
}
