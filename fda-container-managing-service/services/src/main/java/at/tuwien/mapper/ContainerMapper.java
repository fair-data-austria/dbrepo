package at.tuwien.mapper;

import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    default String containerCreateRequestDtoToDockerImage(ContainerCreateRequestDto data) {
        return data.getRepository() + ":" + data.getTag();
    }

    ContainerImage containerCreateRequestDtoToContainerImage(ContainerCreateRequestDto data);

    @Mappings({
            @Mapping(target = "ipAddress.ipv4", source = "ipAddress"),
            @Mapping(target = "created", source = "containerCreated"),
    })
    ContainerDto containerToContainerDto(Container data);

    ContainerBriefDto containerToDatabaseContainerBriefDto(Container data);

    IpAddressDto ipAddressToIpAddressDto(String data);

}
