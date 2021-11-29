package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    Query queryDtoToQuery(QueryDto data);

    QueryDto queryToQueryDto(Query data);

}
