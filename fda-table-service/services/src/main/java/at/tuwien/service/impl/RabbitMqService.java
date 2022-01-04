package at.tuwien.service.impl;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.MessageQueueService;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class RabbitMqService implements MessageQueueService {

    private static final String AMQP_EXCHANGE = "fda";

    private final Channel channel;
    private final TableRepository tableRepository;

    @Autowired
    public RabbitMqService(Channel channel, TableRepository tableRepository) {
        this.channel = channel;
        this.tableRepository = tableRepository;
    }

    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() throws IOException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        final List<Table> tables = tableRepository.findAll();
        for (Table table : tables) {
            create(table);
        }
    }

    @Override
    public void createQueue(Table table) throws AmqpException {
        try {
            create(table);
            log.info("Created queue {} and consumer", table.getTopic());
        } catch (IOException e) {
            log.error("Could not create exchange and consumer: {}", e.getMessage());
            throw new AmqpException("Could not create exchange and consumer", e);
        }
    }

    @Override
    @Transactional
    public void create(Database database) throws IOException {
        channel.exchangeDeclare(database.getExchange(), BuiltinExchangeType.FANOUT, true);
        log.debug("declare fanout exchange {}", database.getExchange());
        channel.exchangeBind(database.getExchange(), AMQP_EXCHANGE, database.getExchange());
        log.debug("bind exchange {} to {}", database.getExchange(), AMQP_EXCHANGE);
        log.info("Declared database exchange {} and bound to root exchange {}", database.getExchange(), AMQP_EXCHANGE);
    }

    @Override
    @Transactional
    public void create(Table table) throws IOException {
        channel.queueDeclare(table.getTopic(), true, false, false, null);
        log.debug("declare queue {}", table.getTopic());
        channel.queueBind(table.getTopic(), table.getDatabase().getExchange(), table.getTopic());
        log.debug("bind queue {} to {}", table.getTopic(), table.getDatabase().getExchange());
        log.info("Declared queue {} and bound to database exchange {}", table.getTopic(), table.getDatabase().getExchange());
    }

}
