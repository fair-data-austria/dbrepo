package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseDto;
import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    @Mappings({
            @Mapping(target = "container", ignore = true)
    })
    DatabaseDto databaseToDatabaseDto(Database data);

}
