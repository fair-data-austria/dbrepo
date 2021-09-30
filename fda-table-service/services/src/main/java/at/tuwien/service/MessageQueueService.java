package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.AmqpException;

import java.io.IOException;

public interface MessageQueueService {

    /**
     * Declares the exchange, the topics and the consumers
     *
     * @throws IOException Error on any of these actions.
     */
    void init() throws IOException;

    /**
     * Creates a queue/topic for a table.
     *
     * @param table The table.
     * @throws AmqpException Creation failed.
     */
    void createQueue(Table table) throws AmqpException;

    /**
     * Creates an exchange for a database.
     *
     * @param database The database.
     * @throws IOException Creation failed.
     */
    void create(Database database) throws IOException;

    /**
     * Creates a queue for a table.
     *
     * @param table The table.
     * @throws IOException Creation failed.
     */
    void create(Table table) throws IOException;

    /**
     * Creates a consumer for a table.
     *
     * @param table The table.
     * @throws IOException Creation failed.
     */
    void createUserConsumer(Table table) throws IOException;
}
