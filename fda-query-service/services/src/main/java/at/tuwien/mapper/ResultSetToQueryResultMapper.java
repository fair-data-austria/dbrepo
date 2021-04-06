package at.tuwien.mapper;

import at.tuwien.entity.QueryResult;

import java.util.List;
import java.util.Map;

public class ResultSetToQueryResultMapper {


    public QueryResult map(List<Map<String, Object>> resultListOfMaps){
        QueryResult queryResult = new QueryResult();
        queryResult.setResult(resultListOfMaps);
        return queryResult;
    }
}
