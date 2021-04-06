package at.tuwien.service;

import at.tuwien.dto.ExecuteQueryDTO;
import at.tuwien.entity.Query;
import at.tuwien.utils.ReWrittenQueryWrapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class QueryStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStoreService.class);
    private ReWrittenQueryWrapper wrapper;

    public QueryStoreService(ReWrittenQueryWrapper wrapper){
        this.wrapper = wrapper;
    }


    public void storeQuery(ExecuteQueryDTO dto, ResultSet rs) {

        Query query = new Query();
        Date date = new Date();
        query.setExecTimestamp(new Timestamp(date.getTime()));
        query.setReWrittenQuery(wrapper.determineReWrittenQuery(dto));
        query.setQueryHash("xyz");
        query.setResultsetHash("xyzdas");


/*
        Query query = new Query();
        query.setQuery(queryDto.getQuery());
        query.setQueryHash(calculateHash(queryDto.getQuery()));
        query.setResultsetHash(calculateHash(rs.toString()));
        impl.persistQuery(query);*/
    }

    private String calculateHash(String toBeHashed) {
        return DigestUtils.sha512Hex(toBeHashed);
    }

//    private boolean checkIfResultSetExists(ResultSet rs) {
//        String resultSetHash = calculateHash(rs.toString());
//        Optional<Query> queryForResultSetHash = impl.getQueryForResultSetHash(resultSetHash);
//        if (queryForResultSetHash.isPresent()) {
//            LOGGER.info("query ist present in query_store with PID: " + queryForResultSetHash.get().getPid());
//            return true;
//        }
//        return false;
//    }

//    public TableDto resolvePID(int pid) {
//        TableDto dto = new TableDto();
//        Optional<Query> queryForPID = impl.getQueryForPID(pid);
//        if (!queryForPID.isPresent()) {
//            return null;
//        }
//        try {
//            ResultSet resultSet = impl.executeQuery(queryForPID.get().getQuery());
//            if (checkIfResultSetHashIsEqual(resultSet, queryForPID.get())) {
//                dto.setResult(QueryStoreUtils.resultSetToList(resultSet));
//                return dto;
//            }
//            resultSet = impl.executeQuery(new HistoryTableWrapper().determineSqlStmtForHistoryData(queryForPID.get()));
//            dto.setResult(QueryStoreUtils.resultSetToList(resultSet));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return dto;
//    }

 /*   private boolean checkIfResultSetHashIsEqual(ResultSet rs, Query query) {
        String hashedResultSet = DigestUtils.sha512Hex(rs.toString());
        return hashedResultSet.equals(query.getResultsetHash());
    }*/

}
