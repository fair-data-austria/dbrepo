package at.tuwien.service.impl;

import at.tuwien.exceptions.SamlObjectException;
import at.tuwien.exceptions.UserNotFoundException;
import at.tuwien.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class AuthenticationServiceImpl implements SAMLUserDetailsService {

    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        final String id = credential.getNameID().getValue();
        try {
            final at.tuwien.entities.user.User user = save(credential);
        } catch (SamlObjectException e) {
            throw new UsernameNotFoundException("Failed to parse attributes", e);
        }
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return new User(id, "", authorities);
    }

    private String getAttributeValue(SAMLCredential credential, String name) throws SamlObjectException {
        final Attribute attribute = credential.getAttribute(name);
        final List<XMLObject> values = attribute.getAttributeValues();
        if (values.size() == 0) {
            throw new SamlObjectException("Failed to get attribute '" + name + "'");
        }
        final XSString value = (XSString) values.get(0);
        return value.getValue();
    }

    private at.tuwien.entities.user.User save(SAMLCredential credential) throws SamlObjectException {
        final Long oid = Long.parseLong(getAttributeValue(credential, "urn:oid:0.9.2342.19200300.100.1.1"));
        final String lastname = getAttributeValue(credential, "urn:oid:2.5.4.4");
        final String firstname = getAttributeValue(credential, "urn:oid:2.5.4.42");
        final String mail = getAttributeValue(credential, "urn:oid:0.9.2342.19200300.100.1.3");
        try {
            return userService.findByOid(oid);
        } catch (UserNotFoundException e) {
            log.debug("user not present, adding to metadata database");
            final at.tuwien.entities.user.User user = at.tuwien.entities.user.User.builder()
                    .oId(oid)
                    .firstname(firstname)
                    .lastname(lastname)
                    .email(mail)
                    .build();
            log.debug("adding user {}", user);
            return userService.save(user); // TODO mw: is this a good software pattern, please provide input
        }
    }
}
