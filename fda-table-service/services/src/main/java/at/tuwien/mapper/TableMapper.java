package at.tuwien.mapper;

import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.ArbitraryPrimaryKeysException;
import at.tuwien.exception.EntityNotSupportedException;
import org.apache.commons.lang.WordUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableBriefDto tableToTableBriefDto(Table data);

    TableDto tableToTableDto(Table data);

    @Mappings({
            @Mapping(target = "columns", source = "columns"),
            @Mapping(source = "name", target = "name", qualifiedByName = "nameMapping"),
            @Mapping(source = "name", target = "internalName", qualifiedByName = "internalMapping"),
    })
    Table tableCreateDtoToTable(TableCreateDto data);

    @Named("nameMapping")
    default String nameToName(String data) {
        return data;
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

    @Named("columnMapping")
    @Mappings({
            @Mapping(source = "primaryKey", target = "isPrimaryKey"),
            @Mapping(source = "type", target = "columnType"),
            @Mapping(source = "nullAllowed", target = "isNullAllowed"),
            @Mapping(source = "name", target = "name"),
            @Mapping(target = "internalName", expression = "java(nameToInternalName(data.getName()))"),
            @Mapping(source = "checkExpression", target = "checkExpression"),
            @Mapping(source = "foreignKey", target = "foreignKey"),
    })
    TableColumn columnCreateDtoToTableColumn(ColumnCreateDto data);

    default Document tableCreateDtoToDocument(TableCreateDto tableSpecification) throws EntityNotSupportedException, ArbitraryPrimaryKeysException {
        final long primaryKeys = Arrays.stream(tableSpecification.getColumns())
                .filter(ColumnCreateDto::getPrimaryKey)
                .filter(c -> !c.getNullAllowed())
                .count();
        if (primaryKeys != 1) {
            System.err.println("Currently only exactly 1 primary key column is supported that does not allow null values");
            throw new ArbitraryPrimaryKeysException("Currently only exactly 1 primary key column is supported that does not allow null values");
        }
        final Optional<ColumnCreateDto> primaryKey = Arrays.stream(tableSpecification.getColumns())
                .filter(ColumnCreateDto::getPrimaryKey)
                .findFirst();
        if (primaryKey.isEmpty()) {
            System.err.println("Primary key is empty.");
            throw new ArbitraryPrimaryKeysException("Primary key is empty.");
        }

        /* document */
        final Document xml;
        try {
            xml = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new EntityNotSupportedException("could not instantiate the root element", e);
        }

        /* hibernate-mapping */
        Element root = xml.createElement("hibernate-mapping");
        root.setAttribute("package", "at.tuwien.userdb");
        xml.appendChild(root);

        /* class */
        Element table = xml.createElement("class");
        table.setAttribute("name", "Table");
        table.setAttribute("table", nameToInternalName(tableSpecification.getName()));
        root.appendChild(table);

        /* id */
        Element id = xml.createElement("id");
        id.setAttribute("name", nameToCamelCase(primaryKey.get().getName()));
        id.setAttribute("column", nameToInternalName(primaryKey.get().getName()));
        id.setAttribute("type", primaryKey.get().getType().getRepresentation());
        table.appendChild(id);

        Element generator = xml.createElement("generator");
        generator.setAttribute("class", "assigned");
        id.appendChild(generator);

        /* properties */
        for (ColumnCreateDto columnSpecification : tableSpecification.getColumns()) {
            if (columnSpecification.getPrimaryKey()) {
                continue;
            }

            Element property = xml.createElement("property");
            property.setAttribute("name", nameToCamelCase(columnSpecification.getName()));
            property.setAttribute("column", nameToInternalName(columnSpecification.getName()));
            property.setAttribute("not-null", columnSpecification.getNullAllowed() ? "false" : "true");
            property.setAttribute("unique", columnSpecification.getUnique() ? "true": "false");
            property.setAttribute("type", columnSpecification.getType().getRepresentation());
            if (!columnSpecification.getType().equals(ColumnTypeDto.ENUM)) {
                table.appendChild(property);
            } else {
                System.err.println("Enums are currently not supported");
                throw new EntityNotSupportedException("Enums are currently not supported");
            }
            if (columnSpecification.getCheckExpression() == null) {
                table.appendChild(property);
            } else {
                System.err.println("Check expressions are not supported");
                throw new EntityNotSupportedException("Check expressions are not supported");
            }
        }

        return xml;
    }

    /**
     * Contract: {@link #tableCreateDtoToDocument(TableCreateDto)} must be executed first as here no sanity checks are
     * performed, maps a table specification to a Java class represented as String
     *
     * @param tableSpecification The table specification
     * @return The String
     */
    default String tableCreateDtoToString(TableCreateDto tableSpecification) {
        final Optional<ColumnCreateDto> primaryKey = Arrays.stream(tableSpecification.getColumns())
                .filter(ColumnCreateDto::getPrimaryKey)
                .findFirst();

        /* document */
        final StringBuilder content = new StringBuilder("package at.tuwien.userdb;\n\n")
                .append("import lombok.Getter;\n")
                .append("import lombok.Setter;\n")
                .append("import java.sql.Date;\n")
                .append("import java.sql.Blob;\n\n");

        /* class */
        content.append("@Getter\n")
                .append("@Setter\n")
                .append("public class Table {\n\n");

        /* properties */
        for (ColumnCreateDto columnSpecification : tableSpecification.getColumns()) {
            content.append("private ")
                    .append(columnSpecification.getType().getRepresentation())
                    .append(" ")
                    .append(nameToCamelCase(columnSpecification.getName()))
                    .append(";\n\n");
        }

        return content.append("}").toString();
    }

}
