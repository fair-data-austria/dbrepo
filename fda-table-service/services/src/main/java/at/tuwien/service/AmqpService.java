package at.tuwien.service;

import at.tuwien.api.amqp.TupleDto;
import at.tuwien.api.database.DatabaseDto;
import at.tuwien.entities.database.Database;
import at.tuwien.mapper.AmqpMapper;
import at.tuwien.mapper.DatabaseMapper;
import at.tuwien.repository.DatabaseRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

@Log4j2
@Service
public class AmqpService {

    private static final String AMQP_EXCHANGE = "fda";
    private static final String AMQP_QUEUE_DATABASES = "fda.databases";

    private final Channel channel;
    private final AmqpMapper amqpMapper;
    private final ObjectMapper objectMapper;
    private final DatabaseMapper databaseMapper;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public AmqpService(Channel channel, AmqpMapper amqpMapper, ObjectMapper objectMapper,
                       DatabaseMapper databaseMapper, DatabaseRepository databaseRepository) {
        this.channel = channel;
        this.amqpMapper = amqpMapper;
        this.objectMapper = objectMapper;
        this.databaseMapper = databaseMapper;
        this.databaseRepository = databaseRepository;
    }

    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    @PostConstruct
    public void init() throws IOException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare(AMQP_QUEUE_DATABASES, true, false, false, null);
        channel.queueBind(AMQP_QUEUE_DATABASES, AMQP_EXCHANGE, AMQP_QUEUE_DATABASES);
        createDatabaseConsumer();
        final List<Database> databases = databaseRepository.findAll();
        for (Database database : databases) {
            final String exchangeName = amqpMapper.exchangeName(database);
            final String queueName = amqpMapper.queueName(database);
            createExchange(exchangeName);
            createQueue(exchangeName, queueName);
            createUserConsumer(queueName);
        }
    }

    /**
     * Creates a new exchange and a new queue for a database.
     *
     * @param database The database.
     * @throws IOException Exchange or queue was not declarable.
     */
    public void create(Database database) throws IOException {
        final String exchangeName = amqpMapper.exchangeName(database);
        final String queueName = amqpMapper.queueName(database);
        createExchange(exchangeName);
        createQueue(exchangeName, queueName);
    }

    /**
     * Deletes a exchange and the queue for a database.
     *
     * @param database The database.
     * @throws IOException Exchange or queue was not deletable.
     */
    public void delete(Database database) throws IOException {
        final String exchangeName = amqpMapper.exchangeName(database);
        final String queueName = amqpMapper.queueName(database);
        deleteExchange(exchangeName);
        deleteQueue(queueName);
    }

    private void createExchange(String exchangeName) throws IOException {
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        log.debug("declare fanout exchange {}", exchangeName);
        channel.exchangeBind(exchangeName, AMQP_EXCHANGE, exchangeName);
        log.debug("bind exchange {} to {}", exchangeName, AMQP_EXCHANGE);
    }

    private void createQueue(String exchangeName, String queueName) throws IOException {
        channel.queueDeclare(queueName, true, false, false, null);
        log.debug("declare queue {}", queueName);
        channel.queueBind(queueName, exchangeName, queueName + ".*t.*");
        log.debug("bind queue {} to {}", queueName, exchangeName);
    }

    private void deleteExchange(String exchangeName) throws IOException {
        channel.exchangeDelete(exchangeName);
        log.debug("delete exchange {}", exchangeName);
    }

    private void deleteQueue(String queueName) throws IOException {
        channel.queueDelete(queueName);
        log.debug("delete queue {}", queueName);
    }

    private void createUserConsumer(String queueName) throws IOException {
        channel.basicConsume(queueName, true, (consumerTag, response) -> {
            try {
                final TupleDto[] payload = objectMapper.readValue(response.getBody(), TupleDto[].class);
                final Integer databaseId = Integer.parseInt(response.getEnvelope().getRoutingKey().split("\\.")[2].substring(1));
                final Integer tableId = Integer.parseInt(response.getEnvelope().getRoutingKey().split("\\.")[3].substring(1));
                log.debug("create consumer for database id {} table id {}", databaseId, tableId);
            } catch(JsonParseException e) {
                log.warn("Could not parse AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ConnectException e) {
                log.warn("Could not redirect AMQP payload {}", e.getMessage());
                /* ignore */
            }
        }, consumerTag -> {/* */});
        log.debug("declare consumer {}", queueName);
    }

    private void createDatabaseConsumer() throws IOException {
        channel.basicConsume(AMQP_QUEUE_DATABASES, true, (consumerTag, response) -> {
            try {
                final Database database = databaseMapper.DatabaseDtoToDatabase(
                        objectMapper.readValue(response.getBody(), DatabaseDto.class));
                if (database.getDeleted() == null) {
                    create(database);
                } else {
                    delete(database);
                }
            } catch(JsonParseException e) {
                log.warn("Could not parse AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ConnectException e) {
                log.warn("Could not redirect AMQP payload {}", e.getMessage());
                /* ignore */
            }
        }, consumerTag -> {/* */});
        log.debug("declare consumer {}", AMQP_QUEUE_DATABASES);
    }

}
