package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseDto;
import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DatabaseMapper.class);

    Database databaseDtoToDatabase(DatabaseDto data);

}
