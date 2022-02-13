package at.tuwien.service.impl;

import at.tuwien.InsertTableRawQuery;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.QueryService;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class QueryServiceImpl implements QueryService {

    private final QueryMapper queryMapper;
    private final ImageMapper imageMapper;

    @Autowired
    public QueryServiceImpl(QueryMapper queryMapper, ImageMapper imageMapper) {
        this.queryMapper = queryMapper;
        this.imageMapper = imageMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void insert(Table table, TableCsvDto data)
            throws TableMalformedException, ImageNotSupportedException {
        if (data.getData().size() == 0) return;
        var env = StreamExecutionEnvironment.getExecutionEnvironment();
        final String query = queryMapper.tableToRawInsertQuery(table);
        final Container container = table.getDatabase().getContainer();
        final String jdbcUrl = "jdbc:" + container.getImage().getJdbcMethod() + "://" + container.getInternalName() + ":" + container.getImage().getDefaultPort() + "/" + table.getDatabase().getInternalName();
        env.fromElements()
                .addSink(JdbcSink.sink(query, queryMapper.tableToJdbcStatementBuilder(table),
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1000)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl(jdbcUrl)
                                .withDriverName(container.getImage().getDriverClass())
                                .withUsername(imageMapper.containerImageToUsername(container.getImage()))
                                .withPassword(imageMapper.containerImageToPassword(container.getImage()))
                                .build()));
        try {
            env.execute();
        } catch (Exception e) {
            log.error("Failed to insert data into table with id {}", table.getId());
            log.debug("Failed to insert data {} into table {}", data, table);
            throw new TableMalformedException("Failed to insert data", e);
        }

    }

}
