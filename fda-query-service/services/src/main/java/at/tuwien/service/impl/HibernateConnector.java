package at.tuwien.service.impl;

import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.querystore.Column;
import at.tuwien.querystore.Query;
import at.tuwien.querystore.Table;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Log4j2
@Service
public abstract class HibernateConnector {

    private static final Integer MIN_SIZE = 5;
    private static final Integer MAX_SIZE = 500;
    private static final Integer INCREMENT_SIZE = 5;
    private static final Integer TIMEOUT = 1800;
    private static final String SESSION_CONTEXT = "thread";
    private static final String COORDINATOR_CLASS = "jdbc";

    @Transactional
    protected SessionFactory getSessionFactory(Database database) {
        return getSessionFactory(database, false);
    }

    @Transactional
    protected SessionFactory getSessionFactory(Database database, Boolean privileged) {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        log.trace("hibernate jdbc url '{}', privileged: {}", url, privileged ? 'y' : 'n');
        final String username = database.getContainer().getImage().getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(privileged ? ContainerImageEnvironmentItemType.PRIVILEGED_USERNAME : ContainerImageEnvironmentItemType.USERNAME))
                .map(ContainerImageEnvironmentItem::getValue)
                .collect(Collectors.toList())
                .get(0);
        final String password = database.getContainer().getImage().getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(privileged ? ContainerImageEnvironmentItemType.PRIVILEGED_PASSWORD : ContainerImageEnvironmentItemType.PASSWORD))
                .map(ContainerImageEnvironmentItem::getValue)
                .collect(Collectors.toList())
                .get(0);
        log.trace("container image {}", database.getContainer().getImage());
        final Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.driver_class", database.getContainer().getImage().getDriverClass())
                .setProperty("hibernate.dialect", database.getContainer().getImage().getDialect())
                .setProperty("hibernate.current_session_context_class", SESSION_CONTEXT)
                .setProperty("hibernate.transaction.coordinator_class", COORDINATOR_CLASS)
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.c3p0.min_size", String.valueOf(MIN_SIZE))
                .setProperty("hibernate.c3p0.max_size", String.valueOf(MAX_SIZE))
                .setProperty("hibernate.c3p0.acquire_increment", String.valueOf(INCREMENT_SIZE))
                .setProperty("hibernate.c3p0.timeout", String.valueOf(TIMEOUT))
                .addAnnotatedClass(Query.class)
                .addAnnotatedClass(Table.class)
                .addAnnotatedClass(Column.class);
        return configuration.buildSessionFactory();
    }


}
