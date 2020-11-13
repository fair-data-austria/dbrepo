package at.tuwien.querystore;


import at.tuwien.dto.QueryDatabaseDTO;
import at.tuwien.pojo.DatabaseContainer;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QueryStoreService {

    private QueryStoreDaoImpl impl;

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStoreService.class);

    @Autowired
    public QueryStoreService(QueryStoreDaoImpl impl) {
        this.impl = impl;
    }


    public void storeQuery(QueryDatabaseDTO dto, List<Map<String, Object>> rsList, String tableName,DatabaseContainer databaseContainer) {
        if (checkIfResultSetExists(rsList)) {
            return;
        }

        Query query = new Query();
        query.setExecTimestamp(new Timestamp(System.currentTimeMillis()));
        query.setResourceName(tableName);
        query.setQuery(dto.getQuery());
        query.setQueryHash(calculateHash(dto.getQuery()));
        System.out.println(rsList.toString());
        query.setResultsetHash(calculateHash((rsList.toString())));
        impl.persistQuery(query);
    }

    private String calculateHash(String toBeHashed) {
        return DigestUtils.sha512Hex(toBeHashed);
    }

    private boolean checkIfResultSetExists(List<Map<String, Object>> rsList) {
        String resultSetHash = calculateHash(rsList.toString());
        Optional<Query> queryForResultSetHash = impl.getQueryForResultSetHash(resultSetHash);
        if (queryForResultSetHash.isPresent()) {
            LOGGER.info("query ist present in query_store with PID: " + queryForResultSetHash.get().getPid());
            return true;
        }
        return false;
    }

  public TablePojo resolvePID(int pid) {
        TablePojo tablePojo = new TablePojo();
        Optional<Query> queryForPID = impl.getQueryForPID(pid);
        if (!queryForPID.isPresent()) {
            return null;
        }
        try {
            ResultSet resultSet = impl.executeQuery(queryForPID.get().getQuery());
            if (checkIfResultSetHashIsEqual(QueryStoreUtils.resultSetToList(resultSet), queryForPID.get())) {
                tablePojo.setResult(QueryStoreUtils.resultSetToList(resultSet));
                return tablePojo;
            }
            resultSet = impl.executeQuery(new HistoryTableWrapper().determineSqlStmtForHistoryData(queryForPID.get()));
            tablePojo.setResult(QueryStoreUtils.resultSetToList(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tablePojo;
    }

    private boolean checkIfResultSetHashIsEqual(List<Map<String, Object>> resultList, Query query) {
        String hashedResultSet = DigestUtils.sha512Hex(resultList.toString());
        return hashedResultSet.equals(query.getResultsetHash());
    }

}
