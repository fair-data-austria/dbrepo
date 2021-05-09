package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.query.QueryResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryResultDto queryResultToQueryResultDto(QueryResult queryResult);
}
