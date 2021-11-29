package at.tuwien.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
public class AuthenticationService extends SAMLAuthenticationProvider {

    @Override
    public Collection<? extends GrantedAuthority> getEntitlements(SAMLCredential credential, Object userDetail) {
        if (userDetail instanceof ExpiringUsernameAuthenticationToken) {
            return new ArrayList<>(((ExpiringUsernameAuthenticationToken) userDetail)
                    .getAuthorities());
        } else {
            return Collections.emptyList();
        }
    }
}