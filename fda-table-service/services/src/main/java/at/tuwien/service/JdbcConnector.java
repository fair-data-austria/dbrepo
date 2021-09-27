package at.tuwien.service;

import at.tuwien.api.amqp.TupleDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
        log.info("Attempt to connect to '{}'", url);
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
            sql = sql + "WITH SYSTEM VERSIONING;";
            log.debug("With versioning {} ",sql);
            context.fetch(sql);
        } else {
            createTableColumnStep.execute();
        }
    }

    @Transactional
    protected void insertCsv(Table table, TableCsvDto data) throws SQLException, ImageNotSupportedException, TableMalformedException {
        if (data.getData().size() == 0 || (data.getData().size() == 1 && data.getData().get(0).size() == 0)) {
            log.warn("No data provided.");
            throw new TableMalformedException("No data provided");
        }
        if (data.getData().get(0).size() != table.getColumns().size()) {
            log.error("Provided columns differ from table columns found in metadata db.");
            throw new TableMalformedException("Provided columns differ from table columns found in metadata db.");
        }
        final List<Field<?>> headers = tableMapper.tableToFieldList(table);
        log.trace("first row received {}", data.getData().size() > 0 ? data.getData().get(0) : null);
        final DSLContext context = open(table.getDatabase());
        final List<InsertValuesStepN<Record>> statements = new LinkedList<>();
        for (List<Object> row : tableMapper.tableCsvDtoToObjectListList(data, headers)) {
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

    protected QueryResultDto selectAll(Table table, Timestamp timestamp, Integer page, Integer size) throws SQLException, ImageNotSupportedException {
        if (table == null || table.getInternalName() == null) {
            log.error("Could not obtain the table internal name");
            throw new SQLException("Could not obtain the table internal name");
        }
        final DSLContext context = open(table.getDatabase());
        /* For versioning, but with jooq implementation better */
        if(table.getDatabase().getContainer().getImage().getDialect().equals("MARIADB")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT * FROM ");
            stringBuilder.append( table.getInternalName());
            if(timestamp != null) {
                stringBuilder.append(" FOR SYSTEM_TIME AS OF TIMESTAMP'");
                stringBuilder.append(timestamp.toLocalDateTime());
                stringBuilder.append("'");
            }
            if(page != null && size != null) {
                page = Math.abs(page);
                size = Math.abs(size);
                stringBuilder.append(" LIMIT ");
                stringBuilder.append(size);
                stringBuilder.append(" OFFSET ");
                stringBuilder.append(page * size);
            }
            stringBuilder.append(";");
            return queryMapper.recordListToQueryResultDto(context.fetch(stringBuilder.toString()));
        } else {
            return queryMapper.recordListToQueryResultDto(context
                    .selectFrom(table.getInternalName())
                    .fetch());
        }

    }

}
