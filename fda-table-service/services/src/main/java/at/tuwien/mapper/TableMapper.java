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
import org.jooq.*;
import org.jooq.impl.DefaultDataType;
import org.jooq.meta.jaxb.CustomType;
import org.jooq.meta.jaxb.ForcedType;
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

    default String columnCreateDtoToEnumTypeName(TableCreateDto table, ColumnCreateDto data) {
        return "__" + nameToInternalName(nameToColumnName(table.getName())) + "_" + nameToInternalName(nameToColumnName(data.getName()));
    }

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
        final List<Constraint> constraints = new LinkedList<>();
        final CreateTableColumnStep columnStep = context.createTableIfNotExists(nameToInternalName(data.getName()));
        /* types for enum */
        for (ColumnCreateDto column : data.getColumns()) {
            if (!column.getType().equals(ColumnTypeDto.ENUM)) {
                continue;
            }
            context.createType(columnCreateDtoToEnumTypeName(data, column))
                    .asEnum(column.getEnumValues())
                    .execute();
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
            columnStep.column(nameToInternalName(nameToColumnName(column.getName())), dataType);
        }
        /* primary keys */
        constraints.add(constraint("PK_FDA")
                .primaryKey(Arrays.stream(data.getColumns())
                        .filter(ColumnCreateDto::getPrimaryKey)
                        .map(c -> field(nameToInternalName(nameToColumnName(c.getName()))))
                        .toArray(Field[]::new)));
        /* constraints */
        final long count = Arrays.stream(data.getColumns())
                .filter(ColumnCreateDto::getUnique)
                .count();
        if (count > 0) {
            /* primary key constraints */
            Arrays.stream(data.getColumns())
                    .filter(ColumnCreateDto::getUnique)
                    .forEach(c -> constraints.add(constraint("UK_" + nameToInternalName(nameToColumnName(c.getName())))
                            .unique(nameToInternalName(nameToColumnName(c.getName())))));
            /* check constraints */
            if (Arrays.stream(data.getColumns()).anyMatch(c -> Objects.nonNull(c.getCheckExpression()))) {
                throw new ArbitraryPrimaryKeysException("Check constraints currently not supported");
            }
//                    .forEach(c -> constraints.add(constraint("CK_" + nameToInternalName(nameToColumnName(c.getName())))
//                            .check(null)));
            /* foreign key constraints */
            Arrays.stream(data.getColumns())
                    .filter(c -> Objects.nonNull(c.getForeignKey()))
                    .forEach(c -> constraints.add(constraint("FK_" + nameToInternalName(nameToColumnName(c.getName())))
                            .foreignKey(c.getForeignKey())
                            .references(c.getReferences())));
        }
        columnStep.constraints(constraints);
        return columnStep;
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
