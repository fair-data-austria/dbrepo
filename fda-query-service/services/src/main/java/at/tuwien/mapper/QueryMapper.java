package at.tuwien.mapper;

import at.tuwien.api.database.query.ExecuteQueryDTO;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryDto queryToQueryDTO(Query query);

    Query queryDTOtoQuery(ExecuteQueryDTO queryDTO);
}
