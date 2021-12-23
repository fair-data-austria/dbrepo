package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.ImageNotSupportedException;
import org.jooq.Field;
import org.jooq.Record;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(QueryMapper.class);

    QueryResultDto queryResultToQueryResultDto(QueryResultDto queryResult);

    default QueryResultDto recordListToQueryResultDto(List<Record> data) {
        final List<Map<String, Object>> result = new LinkedList<>();
        for (Record record : data) {
            final Map<String, Object> map = new HashMap<>();
            for (Field<?> column : record.fields()) {
                log.debug("column name: {}", column);
                map.put(column.getName(), record.get(column.getName()));
            }
            result.add(map);
        }
        return QueryResultDto.builder()
                .result(result)
                .build();
    }

}
