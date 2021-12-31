package at.tuwien.service;

import at.tuwien.entities.database.Database;
import at.tuwien.exception.DatabaseNotFoundException;

public interface DatabaseService {

    /**
     * Finds a database in the metadata database by given id
     *
     * @param id The database id.
     * @return The database if found.
     * @throws DatabaseNotFoundException The database is not found.
     */
    Database find(Long id) throws DatabaseNotFoundException;
}
