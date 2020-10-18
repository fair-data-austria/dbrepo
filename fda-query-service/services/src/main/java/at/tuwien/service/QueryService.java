package at.tuwien.service;

import at.tuwien.pojo.DatabaseConnectionDataPOJO;
import at.tuwien.client.FdaContainerManagingClient;
import at.tuwien.dto.QueryDatabaseDTO;
import at.tuwien.persistence.Datasource;
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

    @Autowired
    private Datasource dataSource;
    @Autowired
    private FdaContainerManagingClient containerClient;

    public List<Map<String, Object>> queryDatabase(QueryDatabaseDTO dto) throws SQLException{
        DatabaseConnectionDataPOJO databaseConnectionDataPOJO = containerClient.getDatabaseConnectionDataPOJO(dto);
        
        return resultSetToList(dataSource.executeQuery(dto,databaseConnectionDataPOJO));
    }

    public  List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
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
