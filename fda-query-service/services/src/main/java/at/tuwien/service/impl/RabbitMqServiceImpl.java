package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.service.MessageQueueService;
import at.tuwien.service.QueryService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ConnectException;

@Log4j2
@Service
public class RabbitMqServiceImpl implements MessageQueueService {

    private final Channel channel;
    private final ObjectMapper objectMapper;
    private final QueryService queryService;

    @Autowired
    public RabbitMqServiceImpl(Channel channel, ObjectMapper objectMapper, QueryService queryService) {
        this.channel = channel;
        this.objectMapper = objectMapper;
        this.queryService = queryService;
    }

    @Override
    @Transactional
    public void createUserConsumer(Table table) throws IOException {
        channel.basicConsume(table.getTopic(), true, (consumerTag, response) -> {
            try {
                queryService.insert(table.getDatabase().getContainer().getId(),
                        table.getDatabase().getId(), table.getId(),
                        objectMapper.readValue(response.getBody(), TableCsvDto.class));
            } catch (JsonParseException | MismatchedInputException e) {
                log.warn("Could not parse AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ConnectException e) {
                log.warn("Could not redirect AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ImageNotSupportedException | TableMalformedException | DatabaseNotFoundException
                    | TableNotFoundException | ContainerNotFoundException e) {
                log.warn("Could not insert AMQP payload {}", e.getMessage());
                if (e.getCause() instanceof DatabaseNotFoundException) {
                    log.error("Database with id {} not found", table.getDatabase().getId());
                }
                if (e.getCause() instanceof ContainerNotFoundException) {
                    log.error("Container with id {} not found", table.getDatabase().getContainer().getId());
                }
                if (e.getCause() instanceof TableNotFoundException) {
                    log.error("Table with id {} not found", table.getDatabase().getId());
                }
                /* ignore */
            }
        }, consumerTag -> {/* */});
        log.debug("declare consumer {}", table);
    }

}
