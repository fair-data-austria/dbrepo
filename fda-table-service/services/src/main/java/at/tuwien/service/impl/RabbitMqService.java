package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.gateway.QueryServiceGateway;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.MessageQueueService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class RabbitMqService implements MessageQueueService {

    private static final String AMQP_EXCHANGE = "fda";

    private final Channel channel;
    private final ObjectMapper objectMapper;
    private final TableRepository tableRepository;
    private final QueryServiceGateway queryServiceGateway;

    @Autowired
    public RabbitMqService(Channel channel, ObjectMapper objectMapper, TableRepository tableRepository,
                           QueryServiceGateway queryServiceGateway) {
        this.channel = channel;
        this.objectMapper = objectMapper;
        this.tableRepository = tableRepository;
        this.queryServiceGateway = queryServiceGateway;
    }

    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() throws IOException, AmqpException {
//        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
//        final List<Table> tables = tableRepository.findAll();
//        for (Table table : tables) {
//            create(table);
//        }
    }

    @Override
    @Transactional(readOnly = true)
    public void create(Table table) throws AmqpException {
        try {
            channel.queueDeclare(table.getTopic(), true, false, false, null);
            channel.queueBind(table.getTopic(), AMQP_EXCHANGE + "." + table.getDatabase().getExchange(),
                    AMQP_EXCHANGE + "." + table.getDatabase().getExchange() + "." + table.getTopic());
        } catch (IOException e) {
            log.error("Failed to create queue and bind for table with id {}", table.getId());
            log.debug("Failed to create queue and bind for table {}", table);
            throw new AmqpException("Failed to create", e);
        }
        log.info("Created queue for table with id {}", table.getId());
        log.debug("created queue for table {}", table);
        try {
            channel.basicConsume(table.getTopic(), true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    final TypeReference<HashMap<String, Object>> payloadReference = new TypeReference<>() {
                    };
                    try {
                        final TableCsvDto data = TableCsvDto.builder()
                                .data(objectMapper.readValue(body, payloadReference))
                                .build();
                        queryServiceGateway.publish(table.getDatabase().getContainer().getId(), table.getDatabase().getId(),
                                table.getId(), data);
                    } catch (IOException e) {
                        log.error("Failed to parse for table with id {}", table.getId());
                        log.debug("Failed to parse for table {} because {}", table, e.getMessage());
                        /* ignore */
                    } catch (HttpClientErrorException.Unauthorized e) {
                        log.error("Failed to authenticate for table with id {}", table.getId());
                        log.debug("Failed to authenticate for table {} because {}", table.getId(), e.getMessage());
                        /* ignore */
                    } catch (HttpClientErrorException.BadRequest e) {
                        log.error("Failed to insert for table with id {}", table.getId());
                        log.debug("Failed to insert for table {} because {}", table.getId(), e.getMessage());
                        /* ignore */
                    }
                }
            });
        } catch (IOException e) {
            log.error("Failed to create consumer for table with id {}", table.getId());
            log.debug("Failed to create basic consumer for table {}", table);
            throw new AmqpException("Failed to create consumer", e);
        } catch (Exception e) {
            log.warn("Failed unknown: {}", e.getMessage());
            /* ignore */
        }
        log.info("Declared consumer for table topic {}", table.getTopic());
        log.debug("declared consumer {}", table);
    }

}
