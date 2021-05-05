package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    DatabaseBriefDto databaseToDatabaseBriefDto(Database data);

    // https://stackoverflow.com/questions/1657193/java-code-library-for-generating-slugs-for-use-in-pretty-urls#answer-1657250
    default String databaseToInternalDatabaseName(Database data) {
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(data.getName()).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

}
