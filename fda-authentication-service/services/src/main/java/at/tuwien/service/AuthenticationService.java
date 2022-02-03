package at.tuwien.service;

import at.tuwien.api.user.JwtResponseDto;
import at.tuwien.api.user.LoginRequestDto;

public interface AuthenticationService {

    /**
     * Authenticates a user with given credentials
     *
     * @param data The credentials
     * @return The token, if successful
     */
    JwtResponseDto authenticate(LoginRequestDto data);
}
