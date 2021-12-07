package at.tuwien.service;


import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.ImageMapper;
import lombok.extern.log4j.Log4j2;

import org.jooq.*;

import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.*;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.sequence;

@Log4j2
public abstract class JdbcConnector {

    private final ImageMapper imageMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

    /**
     * Retrieve the next id from the sequence of a database
     *
     * @param database The database.
     * @return The next id.
     * @throws SQLException
     * @throws ImageNotSupportedException
     */
    protected BigInteger nextSequence(Database database) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(database);
        final Field<BigInteger> id = sequence(name(QueryStoreService.QUERYSTORE_SEQ_NAME))
                .nextval();
        return context.select(id).fetchOne(id);
    }

}
