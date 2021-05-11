package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryResultDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryResultDto queryResultToQueryResultDto(QueryResultDto queryResult);
}
