package at.tuwien.auth;

import at.tuwien.api.user.UserDetailsDto;
import at.tuwien.api.user.UserDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private Integer expire;

    public String generateJwtToken(Authentication authentication) {
        final UserDetailsDto userPrincipal = (UserDetailsDto) authentication.getPrincipal();
        final Algorithm algorithm = Algorithm.HMAC512(secret);
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(expire, ChronoUnit.MILLIS)))
                .sign(algorithm);
    }

    public String getUserNameFromJwtToken(String token) {
        return JWT.decode(token)
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JWT.decode(authToken);
            return true;
        } catch (JWTDecodeException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        }
        return false;
    }
}
