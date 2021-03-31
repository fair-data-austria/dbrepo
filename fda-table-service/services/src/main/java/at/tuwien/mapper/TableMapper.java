package at.tuwien.mapper;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.dto.table.columns.ColumnCreateDto;
import at.tuwien.dto.table.columns.ColumnDto;
import at.tuwien.entity.Table;
import at.tuwien.entity.TableColumn;
import org.mapstruct.*;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableBriefDto tableToTableBriefDto(Table data);

    TableDto tableToTableDto(Table data);

    @Mappings({
            @Mapping(source = "columns", target = "columns", qualifiedByName = "columnMapping"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description"),
    })
    Table tableCreateDtoToTable(TableCreateDto data);

    @Named("columnSlug")
    default String columnNameToString(String column) {
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(column).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
    })
    ColumnDto columnCreateDtoToColumnDto(ColumnCreateDto data);

    @Named("columnMapping")
    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "checkExpression", target = "checkExpression"),
            @Mapping(source = "foreignKey", target = "foreignKey"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

}
