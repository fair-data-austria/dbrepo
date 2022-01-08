package at.tuwien.service.impl;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public abstract class HibernateConnector {

    private static final Integer MIN_SIZE = 5;
    private static final Integer MAX_SIZE = 30;
    private static final Integer INCREMENT_SIZE = 5;
    private static final Integer TIMEOUT = 1800;
    private static final String SESSION_CONTEXT = "thread";
    private static final String COORDINATOR_CLASS = "jdbc";
    private static final String MARIADB_USERNAME = "root";
    private static final String MARIADB_PASSWORD = "mariadb";

    @Transactional
    protected SessionFactory getSessionFactory(Container container) {
        final String url = "jdbc:" + container.getImage().getJdbcMethod() + "://" + container.getInternalName() + "/";
        log.trace("hibernate jdbc url '{}'", url);
        final Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", MARIADB_USERNAME)
                .setProperty("hibernate.connection.password", MARIADB_PASSWORD)
                .setProperty("hibernate.connection.driver_class", container.getImage().getDriverClass())
                .setProperty("hibernate.dialect", container.getImage().getDialect())
                .setProperty("hibernate.current_session_context_class", SESSION_CONTEXT)
                .setProperty("hibernate.transaction.coordinator_class", COORDINATOR_CLASS)
                .setProperty("hibernate.c3p0.min_size", String.valueOf(MIN_SIZE))
                .setProperty("hibernate.c3p0.max_size", String.valueOf(MAX_SIZE))
                .setProperty("hibernate.c3p0.acquire_increment", String.valueOf(INCREMENT_SIZE))
                .setProperty("hibernate.c3p0.timeout", String.valueOf(TIMEOUT));
        return configuration.buildSessionFactory();
    }


}
