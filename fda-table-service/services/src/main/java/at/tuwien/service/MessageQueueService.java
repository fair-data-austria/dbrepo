package at.tuwien.service;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface MessageQueueService {

    /**
     * Declares the exchange, the topics and the consumers
     *
     * @throws IOException Error on any of these actions.
     */
    void init() throws IOException, AmqpException;

    /**
     * Creates a queue and consumer that re-routes the insert requests to the Query Service. Therefore and due to the
     * dependency this method cannot take any input during startup or seeding phase as it would introduce a deadlock.
     * Seeding is solely performed by the Query Service on startup.
     *
     * @param table The table.
     * @throws AmqpException The broker service did not allow to create a consumer.
     */
    void create(Table table) throws AmqpException;
}
