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

    @Mappings({
            @Mapping(target = "repository", expression = "java(data.getImage().contains(\":\") ? data.getImage().substring(0,data.getImage().indexOf(\":\")) : data.getImage())"),
            @Mapping(target = "tag", expression = "java(data.getImage().contains(\":\") ? data.getImage().substring(data.getImage().indexOf(\":\")+1) : \"latest\")"),
    })
    ContainerImage containerCreateRequestDtoToContainerImage(ContainerCreateRequestDto data);

    Container containerToContainer(com.github.dockerjava.api.model.Container data);

    ContainerDto containerToContainerDto(Container data);

    ContainerBriefDto containerToDatabaseContainerBriefDto(Container data);

    IpAddressDto ipAddressToIpAddressDto(String data);

}
