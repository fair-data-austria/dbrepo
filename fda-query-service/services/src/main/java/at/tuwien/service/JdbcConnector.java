package at.tuwien.service;


import at.tuwien.entities.database.Database;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.ImageMapper;
import lombok.extern.log4j.Log4j2;

import org.jooq.*;

import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;

@Log4j2
public abstract class JdbcConnector {

    private final ImageMapper imageMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        log.trace("Attempt to connect to '{}'", url);
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

}
