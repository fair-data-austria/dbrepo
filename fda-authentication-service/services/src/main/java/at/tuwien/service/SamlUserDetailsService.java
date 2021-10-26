package at.tuwien.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class SamlUserDetailsService implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        String id = credential.getNameID().getValue();
        /* right now we only support users */
        return new User(id, "empty", true, true, true, true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
