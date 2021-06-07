package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.TableMapper;
import at.tuwien.utils.ContainerDatabaseUtil;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public abstract class HibernateConnector {

    private final TableMapper tableMapper;

    @Value("${fda.mapping.path}")
    private String mappingPath;

    @Value("${fda.table.path}")
    private String tablePath;

    @Autowired
    public HibernateConnector(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    private Configuration getConfiguration(Database database) throws ImageNotSupportedException {
        final Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", database.getContainer().getImage().getDriverClass());
        configuration.setProperty("hibernate.connection.url", "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName());
        configuration.setProperty("hibernate.connection.username", ContainerDatabaseUtil.getUsername(database));
        configuration.setProperty("hibernate.connection.password", ContainerDatabaseUtil.getPassword(database));
        configuration.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        configuration.setProperty("hibernate.dialect", database.getContainer().getImage().getDialect());
        configuration.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        configuration.setProperty("hibernate.current_session_context_class", "thread");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.mapping", "true");
        configuration.setProperty("hibernate.c3p0.acquire_increment", "1");
        configuration.setProperty("hibernate.c3p0.idle_test_period", "60");
        configuration.setProperty("hibernate.c3p0.idle_test_period", "60");
        configuration.setProperty("hibernate.c3p0.min_size", "1");
        configuration.setProperty("hibernate.c3p0.max_size", "2");
        configuration.setProperty("hibernate.c3p0.max_statements", "50");
        configuration.setProperty("hibernate.c3p0.timeout", "0");
        configuration.setProperty("hibernate.c3p0.acquireRetryAttempts", "1");
        configuration.setProperty("hibernate.c3p0.acquireRetryDelay", "250");
        return configuration;
    }

    private static Session getSession(Configuration configuration) {
        return configuration.buildSessionFactory()
                .getCurrentSession();
    }

    /**
     * Creates a new table with the given table specification from the front-end
     *
     * @param database           The database the table should be created in.
     * @param tableSpecification The table specification.
     * @return The mapped table
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws TableMalformedException     When the specification was not transformable.
     * @throws DataProcessingException     When the database returned some error.
     */
    protected Table createTable(Database database, TableCreateDto tableSpecification) throws ArbitraryPrimaryKeysException,
            ImageNotSupportedException, TableMalformedException, EntityNotSupportedException {
        final Table table = tableMapper.tableCreateDtoToTable(tableSpecification);
        final Document xml = tableMapper.tableCreateDtoToDocument(tableSpecification);
        final String clazz = tableMapper.tableCreateDtoToString(tableSpecification);
        /* debug mapping */
        try {
            final Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            final Source input = new DOMSource(xml);
            final Result output = new StreamResult(new File(mappingPath + "/mapping.xml"));

            final ByteArrayOutputStream mapping = new ByteArrayOutputStream();
            transformer.transform(input, output);
            transformer.transform(input, new StreamResult(mapping));
            table.setMapping(mapping.toByteArray());
            log.debug("Create mapping in {}", mappingPath + "/mapping.xml");
        } catch (TransformerException e) {
            log.error("could not transform mapping: {}", e.getMessage());
            throw new TableMalformedException("could not transform mapping", e);
        }
        /* debug class */
        try {
            final FileWriter fileWriter = new FileWriter(tablePath + "/Table.java");
            fileWriter.append(clazz);
            fileWriter.close();
            table.setDefinition(clazz.getBytes(StandardCharsets.UTF_8));
            log.debug("Create class in {}", tablePath + "/Table.java");
        } catch (IOException e) {
            log.error("could not transform class: {}", e.getMessage());
            throw new TableMalformedException("could not transform class", e);
        }
        /* hibernate session */
        final Configuration configuration = getConfiguration(database);
        configuration.addDocument(xml);
        final Session session = getSession(configuration);
        return table;
    }

    /**
     * Inserts data into an existing table (of a user database running in a Docker container)
     *
     * @param table The table.
     * @param data  The data.
     * @throws ImageNotSupportedException When the image is not supported.
     * @throws DataProcessingException    When the database returned some error.
     */
    protected void insertFromCollection(Table table, Map<String, Collection<String>> data)
            throws ImageNotSupportedException, DataProcessingException {
        final Document xml;
        try {
            xml = tableMapper.byteArrayToDocument(table.getMapping());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new DataProcessingException("Not able to parse the mapping definition byte array");
        }
        final Configuration configuration = getConfiguration(table.getDatabase());
        configuration.addDocument(xml);
        final Session session = getSession(configuration);
        // reflection stuff
    }

    /**
     * Retrieve all rows of a table (of a user database running in a Docker container).
     *
     * @param database The database.
     * @param table    The table.
     * @return The parsed rows.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws DataProcessingException     When the database returned some error.
     */
    protected QueryResultDto getAllRows(Database database, Table table) throws DatabaseConnectionException, DataProcessingException {
        return null;
    }

    /**
     * Deletes a table (of a user database running in a Docker container).
     *
     * @param table The table.
     * @throws DatabaseConnectionException When the connection could not be established.
     * @throws TableMalformedException     When the specification was not transformable.
     * @throws DataProcessingException     When the database returned some error.
     */
    protected void deleteTable(Table table) throws DatabaseConnectionException, TableMalformedException, DataProcessingException, ImageNotSupportedException {
        final Configuration configuration = getConfiguration(table.getDatabase());
        final Session session = getSession(configuration);
        session.delete(table);
    }
}
