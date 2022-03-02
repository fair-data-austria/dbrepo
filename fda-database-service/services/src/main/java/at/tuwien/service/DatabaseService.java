package at.tuwien.service;

import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.api.database.DatabaseModifyDto;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface DatabaseService {
    /**
     * Finds all known databases in the metadata database for a given container id.
     *
     * @param id The container id.
     * @return A list of databases
     */
    List<Database> findAll(Long id);

    /**
     * Finds all known databases in the metadata database.
     *
     * @return List of databases.
     */
    List<Database> findAll();

    /**
     * Finds a specific database for a given id in the metadata database.
     *
     * @param id         The container id.
     * @param databaseId The database id.
     * @return The database if found.
     * @throws DatabaseNotFoundException The database was not found.
     */
    Database findById(Long id, Long databaseId) throws DatabaseNotFoundException, ContainerNotFoundException;

    /**
     * Deletes a database with given id in the metadata database. Side effects: does only mark the database as deleted,
     * does not actually delete it.
     *
     * @param id         The container id.
     * @param databaseId The database id.
     * @throws DatabaseNotFoundException  The database was not found.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws DatabaseMalformedException The query string is malformed.
     * @throws AmqpException              The exchange could not be deleted.
     */
    void delete(Long id, Long databaseId) throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, ContainerNotFoundException;

    /**
     * Creates a new database with minimal metadata in the metadata database and creates a new database on the container.
     *
     * @param id        The container id.
     * @param createDto The metadata.
     * @return The created database as stored on the metadata database.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws ContainerNotFoundException The container was not found.
     * @throws DatabaseMalformedException The query string is malformed.
     * @throws AmqpException              The exchange could not be created.
     */
    Database create(Long id, DatabaseCreateDto createDto) throws ImageNotSupportedException, ContainerNotFoundException,
            DatabaseMalformedException, AmqpException, ContainerConnectionException, UserNotFoundException;

    /**
     * Updates a database with metadata in the metadata database  for a given database id.
     *
     * @param id         The container id.
     * @param databaseId The database id.
     * @param metadata   The metadata.
     * @return The update database, if successful.
     * @throws ContainerNotFoundException   The container was not found.
     * @throws DatabaseMalformedException   The query string is malformed.
     * @throws ContainerConnectionException
     * @throws UserNotFoundException
     */
    Database update(Long id, Long databaseId, DatabaseModifyDto metadata) throws ContainerNotFoundException,
            DatabaseMalformedException, ContainerConnectionException, UserNotFoundException, DatabaseNotFoundException;

    /**
     * Returns a new session for a given {@link Database} entity.
     *
     * @param database The database entity.
     * @return A new session if successful.
     * @throws ContainerConnectionException The container is not reachable from the database service.
     * @throws DatabaseMalformedException   The database is malformed e.g. a session can be created.
     */
    Session getSession(Database database) throws ContainerConnectionException, DatabaseMalformedException;

    /**
     * Returns a new transaction for a given session.
     *
     * @param session The session.
     * @return The transaction if successful.
     * @throws ContainerConnectionException When no connection to the remote database fails (e.g. the container is not running).
     */
    Transaction getTransaction(Session session) throws ContainerConnectionException;
}
