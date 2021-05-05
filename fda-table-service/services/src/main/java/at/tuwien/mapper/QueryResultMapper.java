package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.model.QueryResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryResultMapper {

    QueryResultDto queryResultToQueryResultDto(QueryResult queryResult);
}
