package at.tuwien.service;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface IdentifierService {
    /**
     * Finds all identifiers in the metadata database which are not deleted.
     *
     * @return List of identifiers
     */
    List<Identifier> findAll();

    /**
     * Creates a new identifier in the metadata database which is not yet published
     *
     * @param data The identifier.
     * @return The created identifier from the metadata database if successful.
     * @throws IdentifierPublishingNotAllowedException When the visibility is not self.
     */
    Identifier create(IdentifierDto data) throws IdentifierPublishingNotAllowedException, QueryNotFoundException, RemoteUnavailableException, IdentifierAlreadyExistsException;

    /**
     * Finds a identifier by given id in the metadata database.
     *
     * @param identifierId The identifier id.
     * @return The found identifier from the metadata database if successful.
     * @throws IdentifierNotFoundException The identifier was not found in the metadata database or was deleted.
     */
    Identifier find(Long identifierId) throws IdentifierNotFoundException;

    /**
     * Updated the metadata (only) on the identifier for a given id in the metadata database.
     *
     * @param identifierId The identifier id.
     * @param data         The metadata.
     * @return The updated identifier if successful.
     * @throws IdentifierNotFoundException             TThe identifier was not found in the metadata database or was deleted.
     * @throws IdentifierPublishingNotAllowedException The identifier contained a visibility change which is not allowed here.
     */
    Identifier update(Long identifierId, IdentifierDto data) throws IdentifierNotFoundException, IdentifierPublishingNotAllowedException;

    /**
     * Publishes the identifier for a given identifier id in the metadata database.
     *
     * @param identifierId The identifier id.
     * @param visibility   The new visibility.
     * @return The updated identifier from the metadata database.
     * @throws IdentifierNotFoundException         The identifier was not found in the metadata database or was deleted.
     * @throws IdentifierAlreadyPublishedException The identifier is already published (=EVERYONE) and cannot be un-published.
     */
    Identifier publish(Long identifierId, VisibilityTypeDto visibility) throws IdentifierNotFoundException,
            IdentifierAlreadyPublishedException;

    /**
     * Soft-deletes an identifier for a given id in the metadata database. Does not actually remove the entity from the database, but sets it as deleted.
     *
     * @param identifierId The identifier id.
     * @throws IdentifierNotFoundException The identifier was not found in the metadata database or was deleted.
     */
    void delete(Long identifierId) throws IdentifierNotFoundException;
}
