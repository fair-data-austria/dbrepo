package at.tuwien.mapper;

import at.tuwien.dto.QueryDto;
import at.tuwien.entity.Query;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryDto queryToQueryDTO(Query query);
}
