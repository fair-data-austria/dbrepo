package at.tuwien.persistence;

import at.tuwien.dto.CopyCSVIntoTableDTO;
import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.pojo.DatabaseContainer;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Repository
public class Datasource {

    private DataSource postgresDataSource;


    public ResultSet executeQuery(ExecuteQueryDTO dto, DatabaseContainer databaseContainer) {
        configureDatasource(databaseContainer);
        List<Map<String, Object>> resultListOfMaps = null;
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



    public boolean copyCSVIntoTable(CopyCSVIntoTableDTO dto, DatabaseContainer databaseContainer) {
        configureDatasource(databaseContainer);

        try {
            CopyManager mgr = ((PGConnection) postgresDataSource.getConnection()).getCopyAPI();

            BufferedReader in = new BufferedReader(new FileReader(new File(dto.getPathToCSVFile())));
            //String[] splittedHeader = in.readLine().split(",");
            in.readLine(); // skip the header line
            String command = "COPY %s(%s) FROM STDIN WITH CSV DELIMITER '%s'";
            command = String.format(command,dto.getTableName(), dto.getColumnNames(), dto.getDelimiter());
            mgr.copyIn(command, in);

        } catch (Exception e) {
            e.printStackTrace();
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
