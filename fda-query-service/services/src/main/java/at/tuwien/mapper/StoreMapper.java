package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryDto;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreMapper.class);

    default String createRawQueryStoreSequenceQuery() {
        final String query = "CREATE SEQUENCE IF NOT EXISTS `seq_querystore_id` START WITH 1 INCREMENT BY 1;";
        log.trace("create store seq '{}'", query);
        return query;
    }

    default String createRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS `userdb_querystore` (")
                .append("id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXTVAL(`seq_querystore_id`),")
                .append("doi VARCHAR(255), ")
                .append("title VARCHAR(255) NOT NULL, ")
                .append("description TEXT NOT NULL, ")
                .append("query TEXT NOT NULL, ")
                .append("query_hash VARCHAR(255), ")
                .append("execution_timestamp TIMESTAMP, ")
                .append("result_hash VARCHAR(255), ")
                .append("result_number BIGINT, ")
                .append("created_at TIMESTAMP DEFAULT NOW());");
        log.trace("create store '{}'", query);
        return query.toString();
    }

    default String findOneRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("SELECT ")
                .append(String.join(",", List.of("`id`", "`doi`", "`title`", "`description`", "`query`", "`query_hash`", "`execution_timestamp`",
                        "`result_hash`", "`result_number`", "`created_at`")))
                .append(" FROM `userdb_querystore` WHERE `id` = ?1");
        log.trace("find one '{}'", query);
        return query.toString();
    }

    default String findAllRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("SELECT ")
                .append(String.join(",", List.of("`id`", "`doi`", "`title`", "`description`", "`query`", "`query_hash`", "`execution_timestamp`",
                        "`result_hash`", "`result_number`", "`created_at`")))
                .append(" FROM `userdb_querystore`;");
        log.trace("find all '{}'", query);
        return query.toString();
    }

    default String deleteRawQueryStoreSequenceQuery() {
        final String query = "DROP SEQUENCE IF EXISTS `seq_querystore_id`;";
        log.trace("delete store seq '{}'", query);
        return query;
    }

    default String insertRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("INSERT INTO `userdb_querystore` (")
                .append(String.join(",", List.of("`doi`", "`title`", "`description`", "`query`", "`query_hash`", "`execution_timestamp`",
                        "`result_hash`", "`result_number`")))
                .append(") VALUES (?1) RETURNING id");
        log.trace("insert '{}'", query);
        return query.toString();
    }

    default String quote(String data) {
        if (data == null) {
            return null;
        }
        return "'" + data + "'";
    }

    default String deleteRawQueryStoreQuery() {
        final String query = "DROP TABLE IF EXISTS `userdb_querystore`;";
        log.trace("delete store '{}'", query);
        return query;
    }

}
