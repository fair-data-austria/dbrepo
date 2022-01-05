package at.tuwien.service;

import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;

import java.util.List;

public interface DatabaseService {
    /**
     * Finds all known databases in the metadata database.
     *
     * @return A list of databases
     */
    List<Database> findAll();

    /**
     * Finds a specific database for a given id in the metadata database.
     *
     * @param databaseId The database id.
     * @return The database if found.
     * @throws DatabaseNotFoundException The database was not found.
     */
    Database findById(Long databaseId) throws DatabaseNotFoundException;

    /**
     * Deletes a database with given id in the metadata database. Side effects: does only mark the database as deleted,
     * does not actually delete it.
     *
     * @param databaseId The database id.
     * @throws DatabaseNotFoundException  The database was not found.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DatabaseMalformedException The query string is malformed.
     * @throws AmqpException              The exchange could not be deleted.
     */
    void delete(Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException;

    /**
     * Creates a new database with minimal metadata in the metadata database and creates a new database on the container.
     *
     * @param createDto The metadata.
     * @return The created database as stored on the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws ContainerNotFoundException The container was not foudn.
     * @throws DatabaseMalformedException The query string is malformed.
     * @throws AmqpException              The exchange could not be created.
     */
    Database create(DatabaseCreateDto createDto) throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException;
}
