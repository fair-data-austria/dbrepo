package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.mapper.AmqpMapper;
import at.tuwien.repository.DatabaseRepository;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Log4j2
@Service
@Transactional
public class AmqpService {

    private final String AMQP_EXCHANGE = "fda";

    private final DatabaseRepository databaseRepository;
    private final DeliverCallback deliverCallback;
    private final AmqpMapper amqpMapper;
    private final Channel channel;

    @Autowired
    public AmqpService(DatabaseRepository databaseRepository, DeliverCallback deliverCallback, AmqpMapper amqpMapper,
                       Channel channel) {
        this.databaseRepository = databaseRepository;
        this.deliverCallback = deliverCallback;
        this.amqpMapper = amqpMapper;
        this.channel = channel;
    }

    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    @PostConstruct
    public void init() throws IOException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        final List<Database> databases = databaseRepository.findAll();
        for (Database database : databases) {
            final String exchangeName = amqpMapper.exchangeName(database);
            final String queueName = amqpMapper.queueName(database);
            createExchange(exchangeName);
            createQueue(exchangeName, queueName);
            createConsumer(queueName);
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
        createConsumer(queueName);
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
        channel.exchangeDelete(exchangeName);
        log.debug("delete exchange {}", exchangeName);
        channel.queueDelete(queueName);
        log.debug("delete queue {}", queueName);
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
        channel.queueBind(queueName, exchangeName, queueName);
        log.debug("bind queue {} to {}", queueName, exchangeName);
    }

    private void createConsumer(String queueName) throws IOException {
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {/* */});
        log.debug("declare consumer {}", queueName);
    }

}
