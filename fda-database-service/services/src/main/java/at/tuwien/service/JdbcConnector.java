package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.mapper.ImageMapper;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class JdbcConnector {

    private final ImageMapper imageMapper;
    private final DatabaseMapper databaseMapper;

    protected JdbcConnector(ImageMapper imageMapper, DatabaseMapper databaseMapper) {
        this.imageMapper = imageMapper;
        this.databaseMapper = databaseMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/";
        final Properties properties = imageMapper.containerImageToProperties(database.getContainer().getImage());
        final Connection connection = DriverManager.getConnection(url, properties);
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

    protected void create(Database database) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(database);
        context.createDatabase(databaseMapper.databaseToInternalDatabaseName(database))
                .execute();
    }

    protected void modify(Database old, Database database) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(old);
        final AlterDatabaseStep step = context.alterDatabase(old.getInternalName());
        step.renameTo(database.getName())
                .execute();
    }

    protected void delete(Database database) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(database);
        context.dropDatabase(database.getInternalName())
        .execute();
    }

}
