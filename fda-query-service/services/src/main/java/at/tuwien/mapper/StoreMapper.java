package at.tuwien.mapper;

import org.mapstruct.Mapper;

@Deprecated
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

    default String deleteRawQueryStoreSequenceQuery() {
        final String query = "DROP SEQUENCE IF EXISTS `seq_querystore_id`;";
        log.trace("delete store seq '{}'", query);
        return query;
    }

    default String deleteRawQueryStoreQuery() {
        final String query = "DROP TABLE IF EXISTS `userdb_querystore`;";
        log.trace("delete store '{}'", query);
        return query;
    }

}
