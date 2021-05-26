package at.tuwien.mapper;

import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import org.mapstruct.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableBriefDto tableToTableBriefDto(Table data);

    TableDto tableToTableDto(Table data);

    @Mappings({
            @Mapping(source = "columns", target = "columns", qualifiedByName = "columnMapping"),
            @Mapping(source = "name", target = "name"),
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
            @Mapping(target = "internalName", expression = "java(columnNameToString(data.getName()))"),
            @Mapping(source = "checkExpression", target = "checkExpression"),
            @Mapping(source = "foreignKey", target = "foreignKey"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

    default Document tableCreateDtoToDocument(TableCreateDto tableSpecification) throws ParserConfigurationException, ArbitraryPrimaryKeysException {
        final Stream<ColumnCreateDto> primaryKeys = Arrays.stream(tableSpecification.getColumns())
                .filter(ColumnCreateDto::getPrimaryKey);
        if (primaryKeys.count() != 1 || primaryKeys.findFirst().isEmpty()) {
            throw new ArbitraryPrimaryKeysException("Currently only exactly 1 primary key column is supported");
        }

        /* document */
        final Document xml = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

        /* hibernate-mapping */
        Element root = xml.createElement("hibernate-mapping");
        root.setAttribute("package", "at.tuwien.userdb");

        /* class */
        Element table = xml.createElement("class");
        table.setAttribute("name", tableSpecification.getName());
        table.setAttribute("table", columnNameToString(tableSpecification.getName()));

        /* id */
        Element id = xml.createElement("id");
        id.setAttribute("name", primaryKeys.findFirst().get().getName());
        id.setAttribute("column", columnNameToString(primaryKeys.findFirst().get().getName()));
        Element generator = xml.createElement("generator");
        generator.setAttribute("class", "assigned");
        id.appendChild(generator);
        table.appendChild(id);

        /* properties */
        for (ColumnCreateDto columnSpecification : tableSpecification.getColumns()) {
            Element property = xml.createElement("property");
            property.setAttribute("name", columnSpecification.getName());
            property.setAttribute("column", columnNameToString(columnSpecification.getName()));
            property.setAttribute("type", columnSpecification.getType().toString());
            table.appendChild(property);
        }
        return xml;
    }

}
