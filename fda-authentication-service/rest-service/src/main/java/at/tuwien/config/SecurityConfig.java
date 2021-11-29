package at.tuwien.config;

import at.tuwien.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.SAMLAuthenticationProvider;

@Configuration
public class SecurityConfig {

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        return new AuthenticationService();
    }

}
