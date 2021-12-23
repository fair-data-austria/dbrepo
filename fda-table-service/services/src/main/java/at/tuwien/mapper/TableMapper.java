package at.tuwien.mapper;

import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import org.apache.commons.lang.WordUtils;
import org.jooq.*;
import org.jooq.impl.DefaultDataType;
import org.jooq.meta.jaxb.CustomType;
import org.jooq.meta.jaxb.ForcedType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.*;
import java.util.Comparator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.jooq.impl.SQLDataType.*;
import static org.jooq.impl.DSL.*;

@Mapper(componentModel = "spring")
public interface TableMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TableMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())")
    })
    TableBriefDto tableToTableBriefDto(Table data);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())"),
            @Mapping(target = "topic", expression = "java(data.getTopic())"),
            @Mapping(source = "description", target = "description"),
    })
    TableDto tableToTableDto(Table data);

    @Mappings({
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())"),
            @Mapping(target = "unique", source = "isUnique"),
            @Mapping(target = "checkExpression", expression = "java(data.getCheckExpression())"),
            @Mapping(target = "foreignKey", expression = "java(data.getForeignKey())")
    })
    ColumnDto tableColumnToColumnDto(TableColumn data);

    @Mappings({
            @Mapping(source = "data.name", target = "name"),
            @Mapping(source = "data.name", target = "internalName", qualifiedByName = "internalMapping"),
            @Mapping(source = "data.description", target = "description"),
            @Mapping(source = "database.id", target = "tdbid"),
            @Mapping(source = "database", target = "database"),
    })
    Table tableCreateDtoToTable(Database database, TableCreateDto data);

    @Mappings({
            @Mapping(source = "table.id", target = "tid"),
            @Mapping(source = "table.database.id", target = "cdbid"),
            @Mapping(source = "table", target = "table"),
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "data.name", target = "name"),
            @Mapping(source = "data.internalName", target = "internalName"),
            @Mapping(source = "data.created", target = "created"),
            @Mapping(source = "data.lastModified", target = "lastModified"),
    })
    TableColumn tableColumnToTableColumn(Table table, TableColumn data);

    @Named("internalMapping")
    default String nameToInternalName(String data) {
        if (data == null || data.length() == 0) {
            return data;
        }
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(data).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "unique", target = "isUnique"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
            @Mapping(source = "name", target = "name"),
            @Mapping(target = "internalName", expression = "java(nameToInternalName(data.getName()))"),
            @Mapping(source = "checkExpression", target = "checkExpression"),
            @Mapping(source = "foreignKey", target = "foreignKey"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

    default String tableNameToSequenceName(TableCreateDto data) {
        return "seq_" + nameToInternalName(data.getName());
    }

    default String columnTypeDtoToDataType(ColumnCreateDto data) {
        if (data.getPrimaryKey()) {
            if (data.getType().equals(ColumnTypeDto.NUMBER)) {
                return "BIGINT UNSIGNED";
            }
            if (data.getType().equals(ColumnTypeDto.BLOB)) {
                return "BLOB(65535)";
            }
            if (data.getType().equals(ColumnTypeDto.TEXT)) {
                return "TEXT(65535)";
            }
        }
        switch (data.getType()) {
            case BLOB:
                return "BLOB";
            case DATE:
                return "DATE";
            case TEXT:
                return "TEXT";
            case STRING:
                return "VARCHAR(255)";
            case NUMBER:
                return "BIGINT";
            case DECIMAL:
                return "DOUBLE";
            case BOOLEAN:
                return "BOOLEAN";
            case ENUM:
//                return DefaultDataType.getDefaultDataType(columnCreateDtoToEnumTypeName(table, data));
                return "OTHER";
            default:
                return "OTHER";
        }
    }

    /**
     * Map the table to a drop table query
     * TODO for e.g. postgres image
     *
     * @param data The table
     * @return The drop table query
     */
    default String tableToDropTableRawQuery(Table data) throws ImageNotSupportedException {
        if (!data.getDatabase().getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        return "DROP TABLE `" + data.getInternalName() + "`;";
    }

    /**
     * Map the table to a create table query
     * TODO for e.g. postgres image
     *
     * @param database The database
     * @param data     The table
     * @return The create table query
     */
    default String tableToCreateTableRawQuery(Database database, TableCreateDto data) throws ImageNotSupportedException {
        if (!database.getContainer().getImage().getRepository().equals("mariadb")) {
            throw new ImageNotSupportedException("Currently only MariaDB is supported");
        }
        final StringBuilder query = new StringBuilder("CREATE TABLE `")
                .append(nameToInternalName(data.getName()))
                .append("` (");
        /* create columns */
        for (int i = 0; i < data.getColumns().length; i++) {
            final ColumnCreateDto column = data.getColumns()[i];
            query.append("`")
                    .append(nameToInternalName(column.getName()))
                    .append("` ")
                    .append(columnTypeDtoToDataType(column))
                    .append(column.getNullAllowed() ? " NULL" : " NOT NULL")
                    .append(i != data.getColumns().length - 1 ? ", " : "");
        }
        /* create constraints */
        /* create indices */
        query.append(") WITH SYSTEM VERSIONING;");
        log.debug("create table query built with {} columns and system versioning", data.getColumns().length);
        log.trace("raw create table query: [{}]", query);
        return query.toString();
    }

}
