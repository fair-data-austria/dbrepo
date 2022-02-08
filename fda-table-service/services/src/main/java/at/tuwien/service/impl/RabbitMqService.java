package at.tuwien.service.impl;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;
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
    public void init() throws IOException, AmqpException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        final List<Table> tables = tableRepository.findAll();
        for (Table table : tables) {
            create(table);
        }
    }

    @Override
    @Transactional
    public void create(Table table) throws AmqpException {
        try {
            channel.queueDeclare(table.getTopic(), true, false, false, null);
            channel.queueBind(table.getTopic(), table.getDatabase().getExchange(), table.getTopic());
        } catch (IOException e) {
            log.error("Failed to create queue and bind for table with id {}", table.getId());
            log.debug("Failed to create queue and bind for table {}", table);
            throw new AmqpException("Failed to create", e);
        }
        log.info("Created queue for database with id {}", table.getId());
        log.debug("declare queue for table {}", table);
        log.debug("bind queue to {}", table.getDatabase().getExchange());
    }

}
