package at.tuwien.service.impl;

import at.tuwien.config.FlinkConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.service.StreamService;
import at.tuwien.service.TableService;
import lombok.extern.log4j.Log4j2;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.Tuple1;

@Log4j2
@Service
public class StreamServiceImpl implements StreamService {

    private final QueryMapper queryMapper;
    private final ImageMapper imageMapper;
    private final FlinkConfig flinkConfig;
    private final TableService tableService;

    @Autowired
    public StreamServiceImpl(QueryMapper queryMapper, ImageMapper imageMapper, FlinkConfig flinkConfig,
                             TableService tableService) {
        this.queryMapper = queryMapper;
        this.imageMapper = imageMapper;
        this.flinkConfig = flinkConfig;
        this.tableService = tableService;
    }

    @Override
    @Transactional(readOnly = true)
    @EventListener(ApplicationStartedEvent.class)
    public void init() throws TableNotFoundException, DatabaseNotFoundException, TableMalformedException,
            ImageNotSupportedException {
        /* find */
        final Table table = tableService.find(flinkConfig.getStreamDatabaseId(), flinkConfig.getStreamTableId());
        final String query = queryMapper.tableToRawInsertQuery(table);
        final Container container = table.getDatabase().getContainer();
        final String jdbcUrl = "jdbc:" + container.getImage().getJdbcMethod() + "://" + container.getInternalName() + ":" + container.getImage().getDefaultPort() + "/" + table.getDatabase().getInternalName();
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.socketTextStream("localhost", 9999)
                .flatMap((FlatMapFunction<String, Tuple1<Object[]>>) (input, collector) ->
                        collector.collect(queryMapper.stringToTuple(input)))
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
            env.execute("Stream Insert");
        } catch (Exception e) {
            log.error("Failed to insert tuples");
            throw new TableMalformedException("Failed to insert tuples", e);
        }
    }

}
