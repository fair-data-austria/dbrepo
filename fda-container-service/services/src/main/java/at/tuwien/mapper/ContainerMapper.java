package at.tuwien.mapper;

import at.tuwien.api.container.ContainerBriefDto;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerDto;
import at.tuwien.api.container.ContainerStateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.mapstruct.*;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring", uses = {ImageMapper.class, DatabaseMapper.class})
public interface ContainerMapper {

    default String containerCreateRequestDtoToDockerImage(ContainerCreateRequestDto data) {
        return data.getRepository() + ":" + data.getTag();
    }

    ContainerImage containerCreateRequestDtoToContainerImage(ContainerCreateRequestDto data);

    ContainerDto containerToContainerDto(Container data);

    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    ContainerBriefDto containerToDatabaseContainerBriefDto(Container data);

    @Mappings({
            @Mapping(source = "state", target = "state", qualifiedByName = "containerStateDto"),
            @Mapping(source = "id", target = "hash"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "databases", ignore = true),
    })
    ContainerDto inspectContainerResponseToContainerDto(InspectContainerResponse data);

    @Named("containerStateDto")
    default ContainerStateDto containerStateToContainerStateDto(InspectContainerResponse.ContainerState data) {
        return ContainerStateDto.valueOf(Objects.requireNonNull(data.getStatus()).toUpperCase());
    }

    // https://stackoverflow.com/questions/1657193/java-code-library-for-generating-slugs-for-use-in-pretty-urls#answer-1657250
    default String containerToInternalContainerName(Container data) {
        final Pattern NONLATIN = Pattern.compile("[^\\w-]");
        final Pattern WHITESPACE = Pattern.compile("[\\s]");
        String nowhitespace = WHITESPACE.matcher(data.getName()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return "fda-userdb-" + slug.toLowerCase(Locale.ENGLISH);
    }
}
