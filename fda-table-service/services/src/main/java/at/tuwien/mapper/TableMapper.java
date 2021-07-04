package at.tuwien.mapper;

import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.ImageNotSupportedException;
import org.apache.commons.lang.WordUtils;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.jooq.impl.SQLDataType.*;
import static org.jooq.impl.DSL.*;

@Mapper(componentModel = "spring")
public interface TableMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())")
    })
    TableBriefDto tableToTableBriefDto(Table data);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())")
    })
    TableDto tableToTableDto(Table data);

    @Mappings({
            @Mapping(target = "name", expression = "java(data.getName())"),
            @Mapping(target = "internalName", expression = "java(data.getInternalName())"),
            @Mapping(target = "checkExpression", expression = "java(data.getCheckExpression())"),
            @Mapping(target = "foreignKey", expression = "java(data.getForeignKey())")
    })
    ColumnDto tableColumnToColumnDto(TableColumn data);

    @Mappings({
            @Mapping(source = "columns", target = "columns", qualifiedByName = "columnMapping"),
            @Mapping(source = "name", target = "name", qualifiedByName = "identityMapping"),
            @Mapping(source = "name", target = "internalName", qualifiedByName = "internalMapping"),
            @Mapping(source = "description", target = "description", qualifiedByName = "identityMapping"),
    })
    Table tableCreateDtoToTable(TableCreateDto data);

    @Named("columnMapping")
    default List<TableColumn> columnCreateDtoArrayToTableColumnList(ColumnCreateDto[] data) {
        if (data.length == 0) {
            return new LinkedList<>();
        }
        return Arrays.stream(data)
                .map(this::columnCreateDtoToTableColumn)
                .collect(Collectors.toList());
    }

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

    default String nameToColumnName(String data) {
        return "mdb " + data;
    }

    @Named("camelMapping")
    default String nameToCamelCase(String data) {
        if (data == null || data.length() == 0) {
            return data;
        }
        final String cap = WordUtils.capitalize(data);
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(cap).replaceAll("");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        final String java = NONLATIN.matcher(normalized).replaceAll("");
        char c[] = java.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
    })
    ColumnDto columnCreateDtoToColumnDto(ColumnCreateDto data);

    @Named("identityMapping")
    default String identity(String data) {
        return data;
    }

    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
            @Mapping(source = "name", target = "name", qualifiedByName = "identityMapping"),
            @Mapping(target = "internalName", expression = "java(nameToInternalName(nameToColumnName(data.getName())))"),
            @Mapping(source = "checkExpression", target = "checkExpression", qualifiedByName = "identityMapping"),
            @Mapping(source = "foreignKey", target = "foreignKey", qualifiedByName = "identityMapping"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

    default CreateTableColumnStep tableCreateDtoToCreateTableColumnStep(DSLContext context, TableCreateDto data)
            throws ArbitraryPrimaryKeysException, ImageNotSupportedException {
        if (Arrays.stream(data.getColumns()).noneMatch(ColumnCreateDto::getPrimaryKey)) {
            throw new ArbitraryPrimaryKeysException("There must be at least one primary key column");
        }
        if (Arrays.stream(data.getColumns()).anyMatch(dto -> dto.getCheckExpression() != null)) {
            throw new ImageNotSupportedException("Currently no check operations are supported");
        }
        final CreateTableColumnStep step = context.createTableIfNotExists(nameToInternalName(data.getName()));
        for (ColumnCreateDto column : data.getColumns()) {
            final DataType<?> dataType = columnTypeDtoToDataType(column)
                    .nullable(column.getNullAllowed())
                    .identity(column.getPrimaryKey());
            step.column(nameToInternalName(nameToColumnName(column.getName())), dataType);
        }
        /* constraints */
        return step;
    }

    default List<Field<?>> tableToFieldList(Table data) {
        return data.getColumns()
                .stream()
                .map(c -> field(c.getInternalName()))
                .collect(Collectors.toList());
    }

    default List<List<Object>> tableCsvDtoToObjectListList(TableCsvDto data) {
        return data.getData()
                .stream()
                .map((Function<Map<String, Object>, List<Object>>) stringObjectMap -> new ArrayList<>(stringObjectMap.values()))
                .collect(Collectors.toList());
    }

    default DataType<?> columnTypeDtoToDataType(ColumnCreateDto data) throws ArbitraryPrimaryKeysException {
        if (data.getPrimaryKey()) {
            if (!data.getType().equals(ColumnTypeDto.NUMBER)) {
                throw new ArbitraryPrimaryKeysException("Primary key must be number");
            }
            return BIGINT;
        }
        switch (data.getType()) {
            case BLOB:
                return BLOB(2000);
            case DATE:
                return DATE;
            case TEXT:
            case STRING:
                return LONGVARCHAR;
            case NUMBER:
                return DOUBLE;
            case BOOLEAN:
                return BOOLEAN;
            default:
                return OTHER;
        }
    }

}
