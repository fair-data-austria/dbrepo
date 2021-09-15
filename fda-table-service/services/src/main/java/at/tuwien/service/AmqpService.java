package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.repository.jpa.TableRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Service
public class AmqpService {

    private static final String AMQP_EXCHANGE = "fda";

    private final Channel channel;
    private final DataService dataService;
    private final ObjectMapper objectMapper;
    private final TableRepository tableRepository;

    @Autowired
    public AmqpService(Channel channel, DataService dataService, ObjectMapper objectMapper,
                       TableRepository tableRepository) {
        this.channel = channel;
        this.dataService = dataService;
        this.objectMapper = objectMapper;
        this.tableRepository = tableRepository;
    }

    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() throws IOException {
        channel.exchangeDeclare(AMQP_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        final List<Table> tables = tableRepository.findAll();
        for (Table table : tables) {
            create(table);
            createUserConsumer(table);
        }
    }

    public void createQueue(Table table) throws AmqpException {
        try {
            create(table);
            createUserConsumer(table);
            log.info("Created queue {} and consumer", table.getTopic());
        } catch (IOException e) {
            log.error("Could not create exchange and consumer: {}", e.getMessage());
            throw new AmqpException("Could not create exchange and consumer", e);
        }
    }

    public void createExchange(Database database) throws AmqpException {
        try {
            create(database);
            log.info("Created exchange {}", database.getExchange());
        } catch (IOException e) {
            log.error("Could not create exchange and consumer: {}", e.getMessage());
            throw new AmqpException("Could not create exchange and consumer", e);
        }
    }

    @Transactional
    protected void create(Database database) throws IOException {
        channel.exchangeDeclare(database.getExchange(), BuiltinExchangeType.FANOUT, true);
        log.debug("declare fanout exchange {}", database.getExchange());
        channel.exchangeBind(database.getExchange(), AMQP_EXCHANGE, database.getExchange());
        log.debug("bind exchange {} to {}", database.getExchange(), AMQP_EXCHANGE);
    }

    @Transactional
    protected void create(Table table) throws IOException {
        channel.queueDeclare(table.getTopic(), true, false, false, null);
        log.debug("declare queue {}", table.getTopic());
        channel.queueBind(table.getTopic(), table.getDatabase().getExchange(), table.getTopic());
        log.debug("bind queue {} to {}", table.getTopic(), table.getDatabase().getExchange());
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

    @Transactional
    protected void createUserConsumer(Table table) throws IOException {
        channel.basicConsume(table.getTopic(), true, (consumerTag, response) -> {
            try {
                dataService.insertCsv(table, objectMapper.readValue(response.getBody(), TableCsvDto.class));
            } catch (JsonParseException | MismatchedInputException e) {
                log.warn("Could not parse AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (ConnectException e) {
                log.warn("Could not redirect AMQP payload {}", e.getMessage());
                /* ignore */
            } catch (SQLException | ImageNotSupportedException | DataAccessException | TableMalformedException e) {
                log.warn("Could not insert AMQP payload {}", e.getMessage());
                /* ignore */
            }
        }, consumerTag -> {/* */});
        log.debug("declare consumer {}", table);
    }

}
