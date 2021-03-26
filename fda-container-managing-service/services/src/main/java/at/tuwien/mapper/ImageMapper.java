package at.tuwien.mapper;

import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.api.dto.image.ImageBriefDto;
import at.tuwien.api.dto.image.ImageDto;
import at.tuwien.entity.Architecture;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.NetworkSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageBriefDto containerImageToImageBriefDto(ContainerImage data);

    ImageDto containerImageToImageDto(ContainerImage data);

    @Mappings({
            @Mapping(source = "id", target = "hash"),
            @Mapping(source = "arch", target = "architecture"),
    })
    ContainerImage inspectImageResponseToContainerImage(InspectImageResponse data);

    default Architecture inspectImageResponseToArchitecture(InspectImageResponse data) {
        return Architecture.valueOf(data.getArch());
    }

}
