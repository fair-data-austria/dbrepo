package at.tuwien.persistence;

import at.tuwien.pojo.DatabaseContainer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryStoreDAO {

    private DataSource postgresDataSource;



    public void storeQueryInQueryStore(){
        //configureDatasource(databaseContainer);
        PreparedStatement pStmt = null;
        Connection connection = null;
        try {
            connection = postgresDataSource.getConnection();
            pStmt = connection.prepareStatement("INSERT INTO QUERY_STORE (exec_timestamp,query,re_written_query,query_hash,resultset_hash) VALUES (?,?,?,?)");
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pStmt != null) {
                    pStmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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
