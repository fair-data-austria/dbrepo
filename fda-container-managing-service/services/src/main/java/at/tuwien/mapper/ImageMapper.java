package at.tuwien.mapper;

import at.tuwien.api.dto.image.ImageBriefDto;
import at.tuwien.api.dto.image.ImageDto;
import at.tuwien.api.dto.image.ImageEnvItemDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.EnvironmentItem;
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

    ImageBriefDto containerImageToImageBriefDto(ContainerImage data);

    ImageDto containerImageToImageDto(ContainerImage data);

    @Mappings({
            @Mapping(source = "id", target = "hash"),
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "created", target = "compiled"),
            @Mapping(target = "repository", expression = "java(data.getRepoTags().get(0).substring(0,data.getRepoTags().get(0).indexOf(\":\")))"),
            @Mapping(target = "tag", expression = "java(data.getRepoTags().get(0).substring(data.getRepoTags().get(0).indexOf(\":\")+1))"),
    })
    ContainerImage inspectImageResponseToContainerImage(InspectImageResponse data);

    default Instant dateToInstant(String date) {
        return Instant.parse(date);
    }

    default String[] environmentItemsToStringList(List<EnvironmentItem> data) {
        return data.stream()
                .map(i -> i.getKey() + "=" + i.getValue())
                .toArray(String[]::new);
    }

    default EnvironmentItem imageEnvItemDtoToEnvironmentItem(ImageEnvItemDto data) {
        final EnvironmentItem item = new EnvironmentItem();
        item.setKey(data.getKey());
        item.setValue(data.getValue());
        return item;
    }

    default List<EnvironmentItem> imageEnvironmentItemDtoToEnvironmentItemList(ImageEnvItemDto[] data) {
        return Arrays.stream(data)
                .map(this::imageEnvItemDtoToEnvironmentItem)
                .collect(Collectors.toList());
    }

}
