package at.tuwien.mapper;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryDto queryToQueryDTO(Query query);

    Query queryDTOtoQuery(ExecuteQueryDto queryDTO);
}
