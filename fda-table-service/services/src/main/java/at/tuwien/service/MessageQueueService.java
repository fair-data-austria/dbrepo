package at.tuwien.service;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;

import java.io.IOException;

public interface MessageQueueService {

    /**
     * Declares the exchange, the topics and the consumers
     *
     * @throws IOException Error on any of these actions.
     */
    void init() throws IOException, AmqpException;

    /**
     * Creates a queue for a table.
     *
     * @param table The table.
     * @throws AmqpException Creation failed.
     */
    void create(Table table) throws AmqpException;
}
