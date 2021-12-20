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
            @Mapping(source = "description", target = "description", qualifiedByName = "identityMapping"),
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

    // TODO used?
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

    default String columnCreateDtoToEnumTypeName(TableCreateDto table, ColumnCreateDto data) {
        return nameToInternalName(table.getName()) + "_" + nameToInternalName(data.getName());
    }

    // FIXME
    @Named("identityMapping")
    default String identity(String data) {
        return data;
    }

    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "unique", target = "isUnique"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
            @Mapping(source = "name", target = "name", qualifiedByName = "identityMapping"),
            @Mapping(target = "internalName", expression = "java(nameToInternalName(data.getName()))"),
            @Mapping(source = "checkExpression", target = "checkExpression", qualifiedByName = "identityMapping"),
            @Mapping(source = "foreignKey", target = "foreignKey", qualifiedByName = "identityMapping"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

    default String tableNameToSequenceName(String tableName) {
        return "seq_" + nameToInternalName(tableName);
    }

    /* create sequence nonetheless, if it is used or not */
    default CreateSequenceFlagsStep tableCreateDtoToCreateSequenceFlagsStep(DSLContext context, TableCreateDto data) {
        final String sequenceName = tableNameToSequenceName(data.getName());
        log.trace("create sequence {}", sequenceName);
        return context.createSequenceIfNotExists(sequenceName);
    }

    default CreateTableColumnStep tableCreateDtoToCreateTableColumnStep(DSLContext context, TableCreateDto data)
            throws ArbitraryPrimaryKeysException, ImageNotSupportedException, TableMalformedException {
        final List<Constraint> constraints = new LinkedList<>();
        if (data.getColumns().length == 0) {
            throw new TableMalformedException("The must be at least one column");
        }
        if (Arrays.stream(data.getColumns()).map(ColumnCreateDto::getPrimaryKey).filter(Objects::isNull).count() > 1) {
            log.error("Primary key column must either be true or false, cannot be null");
            throw new ArbitraryPrimaryKeysException("Primary key column must either be true or false, cannot be null");
        }
        if (Arrays.stream(data.getColumns()).noneMatch(ColumnCreateDto::getPrimaryKey)) {
            log.warn("No primary key found, use auto-generated hidden id column");
            final ColumnCreateDto[] newColumns = Arrays.copyOf(data.getColumns(), data.getColumns().length + 1);
            newColumns[data.getColumns().length] = ColumnCreateDto.builder()
                    .name("id")
                    .type(ColumnTypeDto.NUMBER)
                    .checkExpression(null)
                    .autoGenerated(true)
                    .nullAllowed(false)
                    .primaryKey(true)
                    .unique(true)
                    .build();
            data.setColumns(newColumns);
        }
        if (Arrays.stream(data.getColumns()).anyMatch(dto -> dto.getCheckExpression() != null)) {
            // TODO
            log.error("Currently no check operations are supported");
            throw new ImageNotSupportedException("Currently no check operations are supported");
        }
        final CreateTableColumnStep columnStep = context.createTableIfNotExists(nameToInternalName(data.getName()));
        /* types for enum */
        for (ColumnCreateDto column : data.getColumns()) {
            if (!column.getType().equals(ColumnTypeDto.ENUM)) {
                continue;
            }
            throw new ImageNotSupportedException("Currently enums are not supported");
            /* create type */
//            context.createType(columnCreateDtoToEnumTypeName(data, column))
//                    .asEnum(column.getEnumValues())
//                    .execute();
        }
        /* columns */
        for (ColumnCreateDto column : data.getColumns()) {
            if (column.getPrimaryKey()) {
                if (column.getNullAllowed()) {
                    throw new ArbitraryPrimaryKeysException("Primary key cannot be nullable");
                }
                if (column.getType().equals(ColumnTypeDto.ENUM)) {
                    throw new ArbitraryPrimaryKeysException("Primary key cannot be of type enum");
                }
            }
            final DataType<?> dataType = columnTypeDtoToDataType(data, column)
                    .nullable(column.getNullAllowed());
            columnStep.column(nameToInternalName(column.getName()), dataType);
        }
        /* primary keys */
        constraints.add(constraint("pk_" + nameToInternalName(data.getName()))
                .primaryKey(Arrays.stream(data.getColumns())
                        .filter(ColumnCreateDto::getPrimaryKey)
                        .map(this::primaryKeyField)
                        .toArray(Field[]::new)));
        /* constraints */
        final long count = Arrays.stream(data.getColumns())
                .filter(c -> Objects.nonNull(c.getUnique()) && c.getUnique())
                .count();
        if (count > 0) {
            /* primary key constraints */
            Arrays.stream(data.getColumns())
                    .filter(c -> Objects.nonNull(c.getUnique()) && c.getUnique())
                    .forEach(c -> constraints.add(constraint("uk_" + nameToInternalName(c.getName()))
                            .unique(nameToInternalName(c.getName()))));
            /* check constraints */
            if (Arrays.stream(data.getColumns()).anyMatch(c -> Objects.nonNull(c.getCheckExpression()))) {
                throw new ArbitraryPrimaryKeysException("Check constraints currently not supported");
            }
            /* foreign key constraints */
            Arrays.stream(data.getColumns())
                    .filter(c -> Objects.nonNull(c.getForeignKey()))
                    .forEach(c -> constraints.add(constraint("fk_" + nameToInternalName(c.getName()))
                            .foreignKey(c.getForeignKey())
                            .references(c.getReferences())));
        }
        columnStep.constraints(constraints);
        return columnStep;
    }

    default Field<?> primaryKeyField(ColumnCreateDto column) {
        if (column.getType().equals(ColumnTypeDto.TEXT) || column.getType().equals(ColumnTypeDto.BLOB)) {
            return field(sql(nameToInternalName(column.getName()) + " (255)"));
        }
        return field(nameToInternalName(column.getName()));
    }

    default List<Field<?>> tableToFieldList(Table data) {
        return data.getColumns()
                .stream()
                .sorted()
                .map(c -> field(c.getInternalName()))
                .collect(Collectors.toList());
    }

    default List<List<Object>> tableCsvDtoToObjectListList(TableCsvDto data) {
        return data.getData()
                .stream()
                .map((Function<Map<String, Object>, List<Object>>) stringObjectMap -> new ArrayList<>(stringObjectMap.values()))
                .collect(Collectors.toList());
    }

    default DataType<?> columnTypeDtoToDataType(TableCreateDto table, ColumnCreateDto data) {
        if (data.getPrimaryKey()) {
            if (data.getType().equals(ColumnTypeDto.NUMBER)) {
                return BIGINT;
            }
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
            case ENUM:
                return DefaultDataType.getDefaultDataType(columnCreateDtoToEnumTypeName(table, data));
            default:
                return OTHER;
        }
    }

}
