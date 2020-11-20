package at.tuwien.service;

import at.tuwien.client.FdaContainerManagingClient;
import at.tuwien.dto.ExecuteInternalQueryDTO;
import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.mapper.ResultSetToQueryResultMapper;
import at.tuwien.model.QueryResult;
import at.tuwien.persistence.Datasource;
import at.tuwien.pojo.DatabaseContainer;
import at.tuwien.querystore.QueryStoreService;
import at.tuwien.querystore.TablePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {


    private Datasource dataSource;

    private FdaContainerManagingClient containerClient;
    private QueryStoreService storeService;

    @Autowired
    public QueryService(Datasource dataSource, FdaContainerManagingClient containerClient, QueryStoreService storeService) {
        this.dataSource = dataSource;
        this.containerClient = containerClient;
        this.storeService = storeService;
    }


    public QueryResult executeInternalQuery(ExecuteInternalQueryDTO dto)  {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());
        ResultSetToQueryResultMapper mapper = new ResultSetToQueryResultMapper();
        return mapper.map(dataSource.executeQuery(dto, databaseContainer));
    }

    public QueryResult executeExternalQuery(){
        // TODO
        return null;
    }

    public boolean executeStatement(ExecuteStatementDTO dto) {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());
        return dataSource.executeStatement(dto, databaseContainer);
    }

    public TablePojo resolvePID(int pid){
       return storeService.resolvePID(pid);
    }


}
