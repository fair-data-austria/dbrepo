package at.tuwien.gateway;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceGateway {

    /**
     * Validates a token
     *
     * @param token The token
     * @return User details on success
     */
    UserDetails validate(String token);
}
