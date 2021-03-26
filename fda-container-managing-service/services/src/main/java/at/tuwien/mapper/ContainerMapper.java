package at.tuwien.mapper;

import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.NetworkSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    @Mappings({
            @Mapping(source = "id", target = "containerId"),
            @Mapping(source = "created", target = "containerCreated"),
    })
    Container inspectContainerResponseToContainer(InspectContainerResponse containerResponse);

    default String networkSettingsNetworksBridgeToIpAddress(NetworkSettings data) {
        return data.getNetworks().get("bridge").getIpAddress();
    }

    default ContainerImage imageToContainerImage(String combined) {
        int index = combined.indexOf(":");
        final ContainerImage image = new ContainerImage();
        image.setRepository(combined.substring(0, index));
        image.setTag(combined.substring(index + 1));
        return image;
    }

    default String containerCreateRequestDtoToDockerImage(ContainerCreateRequestDto data) {
        return data.getRepository() + ":" + data.getTag();
    }

    ContainerImage containerCreateRequestDtoToContainerImage(ContainerCreateRequestDto data);

    Container containerToContainer(com.github.dockerjava.api.model.Container data);

    ContainerDto containerToContainerDto(Container data);

    ContainerBriefDto containerToDatabaseContainerBriefDto(Container data);

    IpAddressDto ipAddressToIpAddressDto(String data);

}
