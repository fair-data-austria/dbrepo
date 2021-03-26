package at.tuwien.mapper;

import at.tuwien.api.dto.image.ImageBriefDto;
import at.tuwien.api.dto.image.ImageDto;
import at.tuwien.entity.ContainerImage;
import com.github.dockerjava.api.command.InspectImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageBriefDto containerImageToImageBriefDto(ContainerImage data);

    ImageDto containerImageToImageDto(ContainerImage data);

    @Mappings({
            @Mapping(source = "id", target = "hash"),
    })
    ContainerImage inspectImageResponseToContainerImage(InspectImageResponse data);

}
