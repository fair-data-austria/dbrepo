package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.TableMapper;
import at.tuwien.service.DatabaseConnector;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@Log4j2
@Service
public abstract class JdbcConnector implements DatabaseConnector {

    private final ImageMapper imageMapper;
    private final TableMapper tableMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper, TableMapper tableMapper) {
        this.imageMapper = imageMapper;
        this.tableMapper = tableMapper;
    }

    @Override
    public DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        final Properties properties = imageMapper.containerImageToProperties(database.getContainer().getImage());
        final Connection connection = DriverManager.getConnection(url, properties);
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

    @Override
    public void create(Database database, TableCreateDto createDto) throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException, IOException {
        if (isReserved(createDto.getName())) {
            throw new TableMalformedException("Table name contains reserved name");
        }
        if (createDto.getName().isEmpty()) {
            throw new TableMalformedException("Table name is empty");
        }
        final DSLContext context = open(database);
        final CreateSequenceFlagsStep createSequenceFlagsStep = tableMapper.tableCreateDtoToCreateSequenceFlagsStep(context,
                createDto);
        createSequenceFlagsStep.execute();
        log.debug("created id sequence");
        final CreateTableColumnStep createTableColumnStep = tableMapper.tableCreateDtoToCreateTableColumnStep(context,
                createDto);
        log.trace("before execution: {} ", createTableColumnStep.getSQL());
        /* add versioning for mariadb databases */
        if (database.getContainer().getImage().getDialect().equals("MARIADB")) {
            String sql = createTableColumnStep.getSQL();
            sql = sql + " WITH SYSTEM VERSIONING;";
            log.trace("with versioning {} ", sql);
            context.fetch(sql);
        } else {
            createTableColumnStep.execute();
        }
    }

    /**
     * Retrieve the next id from the sequence of a table
     *
     * @param table The table.
     * @return The next id.
     * @throws SQLException
     * @throws ImageNotSupportedException
     */
    protected BigInteger nextSequence(Table table) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(table.getDatabase());
        final Field<BigInteger> id = sequence(name(tableMapper.tableNameToSequenceName(table.getName())))
                .nextval();
        return context.select(id).fetchOne(id);
    }

    @Override
    @Transactional
    public void insertCsv(Table table, TableCsvDto data) throws SQLException, ImageNotSupportedException, TableMalformedException {
        if (data.getData().size() == 0 || (data.getData().size() == 1 && data.getData().get(0).size() == 0)) {
            log.warn("No data provided.");
            throw new TableMalformedException("No data provided");
        }
        if (data.getData().get(0).size() != table.getColumns().size()) {
            log.error("Provided columns differ from table columns found in metadata db.");
            throw new TableMalformedException("Provided columns differ from table columns found in metadata db.");
        }
        final List<Field<?>> headers = tableMapper.tableToFieldList(table);
        log.trace("headers received {}", headers.stream().map(Field::getName).collect(Collectors.toList()));
        log.trace("first row received {}", data.getData().size() > 0 ? data.getData().get(0) : null);
        final DSLContext context = open(table.getDatabase());
        final List<InsertValuesStepN<Record>> statements = new LinkedList<>();
        for (List<Object> row : tableMapper.tableCsvDtoToObjectListList(data)) {
            statements.add(context.insertInto(table(table.getInternalName()), headers)
                    .values(row));
        }
        try {
            context.batch(statements)
                    .execute();
        } catch (DataAccessException e) {
            throw new TableMalformedException("Columns seem to differ or other problem with jOOQ mapper, most commonly it is a data type issue try with type 'STRING'", e);
        }
    }

    /**
     * Deletes a table based on the name.
     *
     * @param table The table.
     * @throws SQLException               Invalid SQL.
     * @throws ImageNotSupportedException Image is not MariaDB.
     */
    @Override
    public void delete(Table table) throws SQLException, ImageNotSupportedException {
        final DSLContext context = open(table.getDatabase());
        context.dropTable(table.getName());
    }

    /**
     * Checks if the word is in the reserved word csv (i.e. an SQL keyword), solves issue 106
     *
     * @param word The word
     * @return True if it is reserved word
     */
    public Boolean isReserved(String word) throws IOException {
        final InputStream stream = new ClassPathResource("mariadb/reserved.csv").getInputStream();
        final List<String> reserved = IOUtils.readLines(stream, "UTF-8");
        return reserved.contains(word.toUpperCase());
    }

}
