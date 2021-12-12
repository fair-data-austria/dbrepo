package at.tuwien.service.impl;

import lombok.extern.log4j.Log4j2;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AuthenticationServiceImpl implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        final List<Attribute> attributes = credential.getAttributes();
        final String id = credential.getNameID().getValue();
        log.debug("user detail id {}", id);
        printAttribute(credential, "oid");
        printAttribute(credential, "sn");
        printAttribute(credential, "givenName");
        printAttribute(credential, "mail");
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return new User(id, "", authorities);
    }

    protected void printAttribute(SAMLCredential credential, String name) {
        if (credential.getAttribute(name) != null) {
            log.debug("Name {}, Value {}", name, credential.getAttributeAsString(name));
        }
    }
}
