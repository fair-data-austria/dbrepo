package at.tuwien.mapper;

import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.api.dto.container.ContainerStateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.mapstruct.*;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    default String containerCreateRequestDtoToDockerImage(ContainerCreateRequestDto data) {
        return data.getRepository() + ":" + data.getTag();
    }

    ContainerImage containerCreateRequestDtoToContainerImage(ContainerCreateRequestDto data);

    @Mappings({
            @Mapping(target = "created", source = "containerCreated")
    })
    ContainerDto containerToContainerDto(Container data);

    ContainerBriefDto containerToDatabaseContainerBriefDto(Container data);

    @Mappings({
            @Mapping(source = "state", target = "state", qualifiedByName = "containerStateDto"),
            @Mapping(source = "id", target = "hash"),
            @Mapping(target = "id", ignore = true)
    })
    ContainerDto inspectContainerResponseToContainerDto(InspectContainerResponse data);

    @Named("containerStateDto")
    default ContainerStateDto containerStateToContainerStateDto(InspectContainerResponse.ContainerState data) {
        return ContainerStateDto.valueOf(Objects.requireNonNull(data.getStatus()).toUpperCase());
    }
}
