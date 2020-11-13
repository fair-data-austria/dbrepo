package at.tuwien.service;

import at.tuwien.dto.ExecuteStatementDTO;
import at.tuwien.pojo.DatabaseContainer;
import at.tuwien.client.FdaContainerManagingClient;
import at.tuwien.dto.QueryDatabaseDTO;
import at.tuwien.persistence.Datasource;
import at.tuwien.querystore.QueryStoreService;
import at.tuwien.querystore.TablePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public List<Map<String, Object>> queryDatabase(QueryDatabaseDTO dto) throws SQLException {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());

        ResultSet resultSet = dataSource.executeQuery(dto, databaseContainer);
        List<Map<String, Object>> resultSetToList = resultSetToList(resultSet);
        storeService.storeQuery(dto,resultSetToList, resultSet.getMetaData().getTableName(1),databaseContainer);
        return resultSetToList;
    }

    public boolean executeStatement(ExecuteStatementDTO dto) {
        DatabaseContainer databaseContainer = containerClient.getDatabaseContainer(dto.getContainerID());
        return dataSource.executeStatement(dto, databaseContainer);
    }

    public TablePojo resolvePID(int pid){
       return storeService.resolvePID(pid);
    }

    public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {

                String colName = rsmd.getColumnName(i);
                Object colVal = rs.getObject(i);
                row.put(colName, colVal);
            }
            rows.add(row);
        }
        return rows;
    }


}
