package at.tuwien.mapper;

import at.tuwien.dto.database.DatabaseBriefDto;
import at.tuwien.entity.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    DatabaseBriefDto databaseToDatabaseBriefDto(Database data);

}
