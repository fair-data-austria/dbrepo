package at.tuwien.service;

import at.tuwien.api.auth.JwtResponseDto;
import at.tuwien.api.auth.LoginRequestDto;

public interface AuthenticationService {

    /**
     * Authenticates a user with given credentials
     *
     * @param data The credentials
     * @return The token, if successful
     */
    JwtResponseDto authenticate(LoginRequestDto data);
}
