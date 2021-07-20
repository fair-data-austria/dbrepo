package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.mapper.TableMapper;
import lombok.extern.log4j.Log4j2;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.jooq.impl.DSL.*;

@Log4j2
public abstract class JdbcConnector {

    private final ImageMapper imageMapper;
    private final TableMapper tableMapper;
    private final QueryMapper queryMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper, TableMapper tableMapper, QueryMapper queryMapper) {
        this.imageMapper = imageMapper;
        this.tableMapper = tableMapper;
        this.queryMapper = queryMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        log.trace("Attempt to connect to '{}'", url);
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

    protected void create(Database database, TableCreateDto createDto) throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final DSLContext context = open(database);
        CreateTableColumnStep createTableColumnStep = tableMapper.tableCreateDtoToCreateTableColumnStep(context, createDto);
        log.debug("Before insertion: {} ", createTableColumnStep.getSQL());
        /* add versioning for mariadb databases */
        if(database.getContainer().getImage().getDialect().equals("MARIADB")) {
            String sql = createTableColumnStep.getSQL();
            sql = sql.substring(0, sql.length() - 1) + ", start TIMESTAMP(6) GENERATED ALWAYS AS ROW START, end TIMESTAMP(6) GENERATED ALWAYS AS ROW END, PERIOD FOR SYSTEM_TIME(start, end)) WITH SYSTEM VERSIONING;";
            log.debug("With versioning {} ",sql);
            context.fetch(sql);
        } else {
            createTableColumnStep.execute();
        }
    }

    protected void insert(Table table, TableCsvDto data) throws SQLException, ImageNotSupportedException {
        if (data.getData().size() == 0) {
            log.warn("No data to insert into table");
            return;
        }
        final List<Field<?>> headers = tableMapper.tableToFieldList(table);
        final DSLContext context = open(table.getDatabase());
        final List<InsertValuesStepN<Record>> statements = new LinkedList<>();
        for (List<Object> row : tableMapper.tableCsvDtoToObjectListList(data)) {
            log.trace("insert row {}", row);
            statements.add(context.insertInto(table(table.getInternalName()), headers)
                    .values(row));
        }
        context.batch(statements)
                .execute();
    }

    protected void delete(Table table) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(table.getDatabase());
        context.dropTable(table.getName());
    }

    protected QueryResultDto selectAll(Table table, Timestamp timestamp) throws SQLException, ImageNotSupportedException {
        if (table == null || table.getInternalName() == null) {
            log.error("Could not obtain the table internal name");
            throw new SQLException("Could not obtain the table internal name");
        }
        final DSLContext context = open(table.getDatabase());
        /* For versioning, but with jooq implementation better */
        if(table.getDatabase().getContainer().getImage().getDialect().equals("MARIADB") && timestamp != null) {
            return queryMapper.recordListToQueryResultDto(context.fetch("SELECT * from " + table.getInternalName() + " FOR SYSTEM_TIME AS OF TIMESTAMP'"+timestamp.toLocalDateTime()+"';"));
        } else {
            return queryMapper.recordListToQueryResultDto(context
                    .selectFrom(table.getInternalName())
                    .fetch());
        }

    }

}
