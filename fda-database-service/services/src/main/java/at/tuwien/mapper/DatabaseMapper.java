package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring", uses = {ContainerMapper.class})
public interface DatabaseMapper {

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
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "engine", expression = "java(data.getContainer().getImage().getRepository()+\":\"+data.getContainer().getImage().getTag())"),
            @Mapping(target = "created", source = "created", dateFormat = "dd-MM-yyyy HH:mm")
    })
    DatabaseBriefDto databaseToDatabaseBriefDto(Database data);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "image", source = "container.image"),
            @Mapping(target = "created", source = "created", dateFormat = "dd-MM-yyyy HH:mm")
    })
    DatabaseDto databaseToDatabaseDto(Database data);

    // https://stackoverflow.com/questions/1657193/java-code-library-for-generating-slugs-for-use-in-pretty-urls#answer-1657250
    default String databaseToInternalDatabaseName(Database data) {
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(data.getName()).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    default String databaseToRawCreateDatabaseQuery(Database database) {
        return "CREATE DATABASE " + database.getInternalName() + ";";
    }

    default String databaseToRawDeleteDatabaseQuery(Database database) {
        return "DROP DATABASE " + database.getInternalName() + ";";
    }

}
