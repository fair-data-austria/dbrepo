package at.tuwien.service;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.TableMapper;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static org.jooq.impl.DSL.*;

public abstract class JdbcConnector {

    private final ImageMapper imageMapper;
    private final TableMapper tableMapper;

    @Autowired
    protected JdbcConnector(ImageMapper imageMapper, TableMapper tableMapper) {
        this.imageMapper = imageMapper;
        this.tableMapper = tableMapper;
    }

    protected DSLContext open(Database database) throws SQLException, ImageNotSupportedException {
        final String url = "jdbc:" + database.getContainer().getImage().getJdbcMethod() + "://" + database.getContainer().getInternalName() + "/" + database.getInternalName();
        final Connection connection = DriverManager.getConnection(url, imageMapper.containerImageToProperties(database.getContainer().getImage()));
        return DSL.using(connection, SQLDialect.valueOf(database.getContainer().getImage().getDialect()));
    }

    protected void create(Database database, TableCreateDto createDto) throws SQLException,
            ArbitraryPrimaryKeysException, ImageNotSupportedException {
        final DSLContext context = open(database);
        tableMapper.tableCreateDtoToCreateTableColumnStep(context, createDto)
                .execute();
    }

    protected void insert(Table table, TableCsvDto data) throws SQLException, ImageNotSupportedException {
        if (data.getData().size() == 0) {
            return;
        }
        final List<Field<?>> headers = tableMapper.tableToFieldList(table);
        final DSLContext context = open(table.getDatabase());
        final List<InsertValuesStepN<Record>> statements = new LinkedList<>();
        for (List<Object> row : tableMapper.tableCsvDtoToObjectListList(data)) {
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

}
