package at.tuwien.mapper;

import at.tuwien.api.database.query.QueryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreMapper.class);

    default String createRawQueryStoreSequenceQuery() {
        return "CREATE SEQUENCE IF NOT EXISTS `seq_querystore_id` START WITH 1 INCREMENT BY 1;";
    }

    default String createRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS `userdb_querystore` (")
                .append("id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXTVAL(`seq_querystore_id`),")
                .append("doi VARCHAR(255) UNIQUE NOT NULL, ")
                .append("title VARCHAR(255) NOT NULL, ")
                .append("query TEXT NOT NULL, ")
                .append("query_hash VARCHAR(255), ")
                .append("execution_timestamp TIMESTAMP WITH TIMEZONE, ")
                .append("result_hash VARCHAR(255), ")
                .append("result_number BIGINT, ")
                .append("created_at TIMESTAMP WITH TIMEZONE DEFAULT NOW());");
        log.trace("create store '{}'", query);
        return query.toString();
    }

    default String findOneRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("SELECT * FROM `userdb_querystore` WHERE id = ?1;");
        log.trace("find one query '{}'", query);
        return query.toString();
    }

    default String findAllRawQueryStoreQuery() {
        final StringBuilder query = new StringBuilder("SELECT * FROM `userdb_querystore`;");
        log.trace("find all query '{}'", query);
        return query.toString();
    }

    default String deleteRawQueryStoreSequenceQuery() {
        return "DROP SEQUENCE `seq_querystore_id`;";
    }

    default String insertRawQueryStoreQuery(QueryDto data) {
        return "";
    }

    default String deleteRawQueryStoreQuery() {
        return "DROP TABLE `userdb_querystore`;";
    }

}
