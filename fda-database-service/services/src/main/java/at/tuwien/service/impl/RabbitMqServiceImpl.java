package at.tuwien.service.impl;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.service.MessageQueueService;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class RabbitMqServiceImpl implements MessageQueueService {

    private static final String AMQP_EXCHANGE = "fda";

    private final Channel channel;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public RabbitMqServiceImpl(Channel channel, DatabaseRepository databaseRepository) {
        this.channel = channel;
        this.databaseRepository = databaseRepository;
    }

    @Override
    @PostConstruct
    public void init() throws IOException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        final List<Database> databases = databaseRepository.findAll();
        for (Database database : databases) {
            create(database);
        }
    }

    @Override
    public void createExchange(Database database) throws AmqpException {
        try {
            create(database);
            log.info("Created exchange {}", database.getExchange());
        } catch (IOException e) {
            log.error("Could not create exchange and consumer: {}", e.getMessage());
            throw new AmqpException("Could not create exchange and consumer", e);
        }
    }

    @Override
    public void deleteExchange(Database database) throws AmqpException {
        try {
            delete(database);
        } catch (IOException e) {
            log.error("Could not delete exchange: {}", e.getMessage());
            throw new AmqpException("Could not delete exchange", e);
        }
    }

    private void create(Database database) throws IOException {
        channel.exchangeDeclare(database.getExchange(), BuiltinExchangeType.FANOUT, true);
        log.debug("declare fanout exchange {}", database.getExchange());
        channel.exchangeBind(database.getExchange(), AMQP_EXCHANGE, database.getExchange());
        log.debug("bind exchange {} to {}", database.getExchange(), AMQP_EXCHANGE);
    }

    private void delete(Database database) throws IOException {
        channel.exchangeDelete(database.getExchange());
        log.debug("delete exchange {}", database.getExchange());
        for (Table table : database.getTables()) {
            delete(table);
        }
    }

    private void delete(Table table) throws IOException {
        channel.queueDelete(table.getTopic());
        log.debug("delete queue {}", table.getTopic());
    }

}
