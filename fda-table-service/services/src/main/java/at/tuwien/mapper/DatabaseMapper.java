package at.tuwien.mapper;

import at.tuwien.api.database.DatabaseDto;
import at.tuwien.entities.database.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DatabaseMapper {

    @Mappings({
    })
    Database DatabaseDtoToDatabase(DatabaseDto data);

}
