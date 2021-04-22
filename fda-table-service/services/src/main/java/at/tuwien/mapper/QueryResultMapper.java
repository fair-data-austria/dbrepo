package at.tuwien.mapper;

import at.tuwien.model.QueryResult;
import at.tuwien.model.QueryResultDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryResultMapper {

    QueryResultDto queryResultToQueryResultDto(QueryResult queryResult);
}
