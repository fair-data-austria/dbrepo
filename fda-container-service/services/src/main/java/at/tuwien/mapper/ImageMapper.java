package at.tuwien.mapper;

import at.tuwien.api.container.image.ImageBriefDto;
import at.tuwien.api.container.image.ImageDateDto;
import at.tuwien.api.container.image.ImageDto;
import at.tuwien.api.container.image.ImageEnvItemDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageDate;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import com.github.dockerjava.api.command.InspectImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mappings({})
    ImageBriefDto containerImageToImageBriefDto(ContainerImage data);

    @Mappings({
            @Mapping(target = "environment", ignore = true), // cannot map since front-end would know credentials
    })
    ImageDto containerImageToImageDto(ContainerImage data);

    @Mappings({
            @Mapping(source = "id", target = "hash"),
            @Mapping(source = "created", target = "compiled"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "repository", expression = "java(data.getRepoTags().get(0).substring(0,data.getRepoTags().get(0).indexOf(\":\")))"),
            @Mapping(target = "tag", expression = "java(data.getRepoTags().get(0).substring(data.getRepoTags().get(0).indexOf(\":\")+1))"),
    })
    ContainerImage inspectImageResponseToContainerImage(InspectImageResponse data);

    ContainerImageEnvironmentItem imageEnvItemDtoToEnvironmentItem(ImageEnvItemDto data);

    default Instant dateToInstant(String date) {
        return Instant.parse(date);
    }

    default String[] environmentItemsToStringList(List<ContainerImageEnvironmentItem> data) {
        return data.stream()
                .map(i -> i.getKey() + "=" + i.getValue())
                .toArray(String[]::new);
    }

    default List<ContainerImageEnvironmentItem> imageEnvironmentItemDtoToEnvironmentItemList(ImageEnvItemDto[] data) {
        return Arrays.stream(data)
                .map(this::imageEnvItemDtoToEnvironmentItem)
                .collect(Collectors.toList());
    }

}
