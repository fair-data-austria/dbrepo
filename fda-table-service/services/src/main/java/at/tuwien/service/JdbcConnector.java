package at.tuwien.service;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.mapper.ImageMapper;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class JdbcConnector {

    private final ImageMapper imageMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    protected DSLContext open(Database database) throws SQLException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        // TODO
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

}
