package at.tuwien.mapper;

import at.tuwien.api.dto.container.DatabaseContainerBriefDto;
import at.tuwien.api.dto.container.DatabaseContainerDto;
import at.tuwien.api.dto.container.IpAddressDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.DatabaseContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.NetworkSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DatabaseContainerMapper {

    @Mappings({
            @Mapping(source = "id", target = "containerId"),
            @Mapping(source = "created", target = "containerCreated"),
    })
    DatabaseContainer inspectContainerResponseToDatabaseContainer(InspectContainerResponse containerResponse);

    default String networkSettingsNetworksBridgeToIpAddress(NetworkSettings data) {
        return data.getNetworks().get("bridge").getIpAddress();
    }

    default ContainerImage imageToContainerImage(String image) {
        int index = image.indexOf(":");
        return new ContainerImage().builder()
                .repository(image.substring(0, index))
                .tag(image.substring(index + 1))
                .build();
    }

    DatabaseContainer containerToDatabaseContainer(Container data);

    DatabaseContainerBriefDto databaseContainerToDatabaseContainerBriefDto(DatabaseContainer data);

    @Mappings({
            @Mapping(target = "ipAddress.ipv4", source = "containerCreated"),
    })
    DatabaseContainerDto databaseContainerToDatabaseContainerDto(DatabaseContainer data);

    IpAddressDto ipAddressToIpAddressDto(String data);

}
