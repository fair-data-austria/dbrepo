package at.tuwien.service;

import at.tuwien.client.FdaContainerManagingClient;
import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.mapper.ResultSetToQueryResultMapper;
import at.tuwien.model.QueryResult;
import at.tuwien.persistence.Datasource;
import at.tuwien.pojo.DatabaseContainer;
import at.tuwien.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {


    private Datasource dataSource;
    private FdaContainerManagingClient containerClient;

    @Autowired
    public QueryService(Datasource dataSource, FdaContainerManagingClient containerClient) {
        this.dataSource = dataSource;
        this.containerClient = containerClient;
    }


    public QueryResult executeQuery(ExecuteQueryDTO dto) {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());
        ResultSetToQueryResultMapper mapper = new ResultSetToQueryResultMapper();
        ResultSet rs = dataSource.executeQuery(dto, databaseContainer);
        List<Map<String, Object>> resultListOfMaps = null;
        try{
            resultListOfMaps = ResultUtil.resultSetToListOfMap(rs);
            rs.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return mapper.map(resultListOfMaps);
    }

    public boolean executeStatement(ExecuteStatementDTO dto) {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());
        return dataSource.executeStatement(dto, databaseContainer);
    }



}
