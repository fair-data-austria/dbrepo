package at.tuwien.service;


import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import lombok.extern.log4j.Log4j2;

import org.jooq.*;
import org.jooq.Record;

import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.List;
import java.util.Properties;

import static org.jooq.impl.DSL.*;
@Log4j2
public abstract class JdbcConnector {

    private final ImageMapper imageMapper;
    private final QueryMapper queryMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper, QueryMapper queryMapper) {
        this.imageMapper = imageMapper;
        this.queryMapper = queryMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        log.info("Attempt to connect to '{}'", url);
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

}
