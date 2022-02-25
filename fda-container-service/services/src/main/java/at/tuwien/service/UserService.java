package at.tuwien.service;

import at.tuwien.entities.user.User;
import at.tuwien.exception.UserNotFoundException;

public interface UserService {

    /**
     * Find a user in the metadata database by username.
     *
     * @param username The username.
     * @return The user, if successful
     * @throws UserNotFoundException The user was not found
     */
    User findByUsername(String username) throws UserNotFoundException;

}
