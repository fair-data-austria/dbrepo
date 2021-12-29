package at.tuwien.service.impl;

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

    private static final Integer POOL_SIZE = 3;
    private static final String SESSION_CONTEXT = "thread";

    @Transactional
    protected SessionFactory getSessionFactory(Database database) {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        log.trace("hibernate jdbc url '{}'", url);
        final String username = database.getContainer().getImage().getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(ContainerImageEnvironmentItemType.USERNAME))
                .map(ContainerImageEnvironmentItem::getValue)
                .collect(Collectors.toList())
                .get(0);
        final String password = database.getContainer().getImage().getEnvironment()
                .stream()
                .filter(e -> e.getType().equals(ContainerImageEnvironmentItemType.PASSWORD))
                .map(ContainerImageEnvironmentItem::getValue)
                .collect(Collectors.toList())
                .get(0);
        final Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.driver_class", database.getContainer().getImage().getDriverClass())
                .setProperty("hibernate.connection.pool_size", String.valueOf(POOL_SIZE))
                .setProperty("hibernate.dialect", database.getContainer().getImage().getDialect())
                .setProperty("hibernate.current_session_context_class", SESSION_CONTEXT);
        return configuration.buildSessionFactory();
    }

    /**
     * Checks if the word is in the reserved word csv (i.e. an SQL keyword), solves issue 106
     *
     * @param word The word
     * @return True if it is reserved word
     */
    public static Boolean isReserved(String word) throws IOException {
        final InputStream stream = new ClassPathResource("mariadb/reserved.csv").getInputStream();
        final List<String> reserved = IOUtils.readLines(stream, "UTF-8");
        return reserved.contains(word.toUpperCase());
    }


}
