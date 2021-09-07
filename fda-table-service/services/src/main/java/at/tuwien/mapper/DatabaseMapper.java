package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.ImageNotSupportedException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Optional;
import java.util.Properties;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    @Mappings({
    })
    Database DatabaseDtoToDatabase(DatabaseDto data);

}
