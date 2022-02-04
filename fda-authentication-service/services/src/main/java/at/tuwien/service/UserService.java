package at.tuwien.service;

import at.tuwien.api.auth.SignupRequestDto;
import at.tuwien.entities.user.User;
import at.tuwien.exception.RoleNotFoundException;
import at.tuwien.exception.UserEmailExistsException;
import at.tuwien.exception.UserNameExistsException;
import at.tuwien.exception.UserNotFoundException;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User find(Long id) throws UserNotFoundException;

    User create(SignupRequestDto user) throws UserEmailExistsException, UserNameExistsException, RoleNotFoundException;
}
