package at.tuwien.service;

import at.tuwien.entities.database.table.Table;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface MessageQueueService {

    /**
     * Creates a consumer for a table.
     *
     * @param table The table.
     * @throws IOException Creation failed.
     */
    void createUserConsumer(Table table) throws IOException;
}
