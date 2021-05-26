package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateConnector implements ContainerDatabaseConnector {

    private static Session getConnection(Database database) {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", database.getContainer().getImage().getDriverClass());
        settings.put("dialect", database.getContainer().getImage().getDialect());
        settings.put("hibernate.connection.url", "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName());
        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "password");
        settings.put("hibernate.current_session_context_class", "thread");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");
        final MetadataSources metadataSources = new MetadataSources(new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build());
        return metadataSources.buildMetadata()
                .getSessionFactoryBuilder()
                .build()
                .getCurrentSession();
    }

    @Override
    public void createTable(Database database, TableCreateDto tableSpecification) throws DatabaseConnectionException, TableMalformedException, DataProcessingException {

    }

    @Override
    public QueryResultDto insertIntoTable(Database database, Table table, List<Map<String, Object>> data, List<String> headers) throws DatabaseConnectionException, DataProcessingException {
        return null;
    }

    @Override
    public QueryResultDto getAllRows(Database database, Table table) throws DatabaseConnectionException, DataProcessingException {
        return null;
    }

    @Override
    public void deleteTable(Table table) throws DatabaseConnectionException, TableMalformedException, DataProcessingException {

    }
}
