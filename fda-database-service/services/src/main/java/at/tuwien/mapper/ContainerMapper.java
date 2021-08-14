package at.tuwien.mapper;

import at.tuwien.api.container.ContainerDto;
import at.tuwien.entities.container.Container;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "databases", source = "databases", ignore = true),
    })
    ContainerDto containerToContainerDto(Container data);

}
