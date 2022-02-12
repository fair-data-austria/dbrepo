package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.DatabaseNotFoundException;

public interface DatabaseService {

    /**
     * Finds a database by given id in the remote database service.
     *
     * @param id The id.
     * @return The database.
     */
    Database find(Long id) throws DatabaseNotFoundException;
}
