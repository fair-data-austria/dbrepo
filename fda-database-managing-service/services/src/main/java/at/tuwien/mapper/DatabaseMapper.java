package at.tuwien.mapper;

import at.tuwien.dto.database.DatabaseBriefDto;
import at.tuwien.entity.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    DatabaseBriefDto databaseToDatabaseBriefDto(Database data);

}
