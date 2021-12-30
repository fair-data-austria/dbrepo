package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;

public interface DatabaseService {

    /**
     * Finds a database by id.
     *
     * @param id The id.
     * @return Return the database if found.
     * @throws DatabaseNotFoundException
     */
    Database findDatabase(Long id) throws DatabaseNotFoundException;
}
