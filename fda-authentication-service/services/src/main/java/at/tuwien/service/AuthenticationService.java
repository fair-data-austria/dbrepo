package at.tuwien.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class AuthenticationService extends SAMLAuthenticationProvider implements SAMLUserDetailsService {

    @Override
    public Collection<? extends GrantedAuthority> getEntitlements(SAMLCredential credential, Object userDetail) {
        if (userDetail instanceof ExpiringUsernameAuthenticationToken) {
            return new ArrayList<>(((ExpiringUsernameAuthenticationToken) userDetail)
                    .getAuthorities());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        final String userID = credential.getNameID().getValue();
        log.debug("Logged in user {}", userID);
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return new User(userID, "<abc123>", true, true, true, true, authorities);
    }
}
