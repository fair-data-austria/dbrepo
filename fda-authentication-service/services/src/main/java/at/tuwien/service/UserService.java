package at.tuwien.service;

import at.tuwien.entities.user.User;
import at.tuwien.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService {

    /**
     * List all users known to the metadata database
     *
     * @return List of users.
     */
    List<User> findAll();

    /**
     * Retrieve a specific user with id that is known to the metadata database
     *
     * @param id The id.
     * @return The user.
     * @throws UserNotFoundException
     */
    User findById(Long id) throws UserNotFoundException;

    /**
     * Retrieve a specific user with oid that is known to the metadata database
     *
     * @param oid The oid.
     * @return The user.
     * @throws UserNotFoundException
     */
    User findByOid(Long oid) throws UserNotFoundException;

    /**
     * Save or update a user in the metadata database
     *
     * @param user The user.
     * @return The saved/updated user.
     */
    User save(User user);
}
