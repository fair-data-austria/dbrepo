package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;

import java.io.IOException;

public interface MessageQueueService {
    /**
     * In case of server downtime this method restores all exchanges and bindings
     *
     * @throws IOException Exchange or queue was not declarable.
     */
    void init() throws IOException;

    /**
     * Creates an exchange for a database.
     *
     * @param database The database.
     * @throws AmqpException Could not create the exchange.
     */
    void createExchange(Database database) throws AmqpException;

    /**
     * Deletes an exchange for a database.
     *
     * @param database The database.
     * @throws AmqpException Could not delete the exchange.
     */
    void deleteExchange(Database database) throws AmqpException;
}