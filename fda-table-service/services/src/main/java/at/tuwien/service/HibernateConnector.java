package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.utils.ContainerDatabaseUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class HibernateConnector implements ContainerDatabaseConnector {

    final TableMapper tableMapper;

    @Autowired
    public HibernateConnector(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    private static Configuration getConfiguration(Database database) throws ImageNotSupportedException {
        final Configuration configuration = new Configuration();
        configuration.setProperty("connection.driver_class", database.getContainer().getImage().getDriverClass());
        configuration.setProperty("dialect", database.getContainer().getImage().getDialect());
        configuration.setProperty("hibernate.connection.url", "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName());
        configuration.setProperty("hibernate.connection.username", ContainerDatabaseUtil.getUsername(database));
        configuration.setProperty("hibernate.connection.password", ContainerDatabaseUtil.getPassword(database));
        configuration.setProperty("hibernate.current_session_context_class", "thread");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.mapping", "true");
        return configuration;
    }

    private static Session getSession(Configuration configuration) {
        return configuration.buildSessionFactory()
                .getCurrentSession();
    }

    @Override
    public void createTable(Database database, TableCreateDto tableSpecification) throws DatabaseConnectionException, TableMalformedException, DataProcessingException, ArbitraryPrimaryKeysException, ParserConfigurationException, ImageNotSupportedException {
        final Document xml = tableMapper.tableCreateDtoToDocument(tableSpecification);
        log.debug("created document {}", xml);
        /* hibernate session */
        final Configuration configuration = getConfiguration(database);
        configuration.addDocument(xml);
        final Session session = getSession(configuration);
        session.flush();
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
