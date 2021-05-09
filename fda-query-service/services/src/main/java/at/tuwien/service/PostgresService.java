package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Log4j2
@Service
public class PostgresService extends JdbcConnector {

    private final Properties postgresProperties;

    @Autowired
    protected PostgresService(Properties postgresProperties) {
        this.postgresProperties = postgresProperties;
    }

    private Connection getConnection(Database database) throws DatabaseConnectionException {
        Connection connection;
        final String URL = "jdbc:postgresql://" + database.getContainer().getInternalName() + ":"
                + database.getContainer().getImage().getDefaultPort() + "/" + database.getInternalName();
        try {
            connection = open(URL, postgresProperties);
        } catch (SQLException e) {
            log.error("Could not connect to the database container, is it running from Docker container? IT DOES NOT WORK FROM IDE! URL: {} Params: {}", URL, postgresProperties);
            throw new DatabaseConnectionException("Could not connect to the database container, is it running?", e);
        }
        return connection;
    }

    public void createQuerystore(Database database) throws DatabaseConnectionException {

        try {
            final PreparedStatement statement = getCreateQueryStoreStatement(getConnection(database));
            statement.execute();
        } catch (SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
        }
    }

    @Override
    List<Query> getQueries(Database database){
        log.debug("Get Queries from Querystore");
        final String getQueries="SELECT id,query, query_normalized, execution_timestamp FROM querystore ORDER BY execution_timestamp desc;";
        List<Query> results = new ArrayList<>();
        try {
            Connection connection = getConnection(database);
            PreparedStatement statement = connection.prepareStatement(getQueries);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                results.add(Query.builder()
                        .id(result.getLong("id"))
                        .query(result.getString("query"))
                        .queryNormalized(result.getString("query_normalized"))
                        .executionTimestamp(result.getTimestamp("execution_timestamp"))
                        .build());
            }
        } catch(DatabaseConnectionException e) {
            log.error("Problem with connecting to the database while selecting from Querystore");
        } catch(SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
        }
        return results;
    }

    @Override
    public Boolean saveQuery(Database database,Query query) {
        log.debug("Save Query {}", query.toString());
        final String saveQuery="INSERT INTO querystore(query,query_normalized, query_hash, execution_timestamp, result_hash, result_number) VALUES(?,?,?,?,?,?);";
        try {
            Connection connection = getConnection(database);
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            System.out.println(query.toString());
            statement.setString(1, "'"+query.getQuery()+"'");
            statement.setString(2, "'"+query.getQueryNormalized()+"'");
            statement.setString(3, query.getQueryHash());
            statement.setTimestamp(4, query.getExecutionTimestamp());
            statement.setString(5, query.getResultHash());
            statement.setInt(6, query.getResultNumber());
            log.debug(statement.toString());
            return statement.execute();

        } catch(SQLException e) {
            log.error("The SQL statement seems to contain invalid syntax");
        } catch(DatabaseConnectionException e) {
            log.error("Problem with connecting to the database while inserting into Querystore");
        }
        return false;
    }

    @Override
    PreparedStatement getCreateQueryStoreStatement(Connection connection) throws SQLException {
        log.debug("create querystore");
        final String createQuery="CREATE TABLE IF NOT EXISTS querystore (" +
                "                id serial PRIMARY KEY," +
                "                query text," +
                "                query_normalized text," +
                "                query_hash text," +
                "                execution_timestamp timestamp," +
                "                result_hash text," +
                "                result_number integer" +
                "                );";
        log.debug("compiled query as \"{}\"", createQuery);
        return connection.prepareStatement(createQuery);
    }



}
