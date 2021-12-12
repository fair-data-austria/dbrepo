package at.tuwien.service;

import at.tuwien.exceptions.SamlObjectException;
import lombok.extern.log4j.Log4j2;
import org.apache.xmlbeans.SimpleValue;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class UserService implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        Long oid;
        String surname;
        String firstname;
        String mail;
        try {
            oid = getLong(credential, "oid");
            surname = getString(credential, "sn");
            firstname = getString(credential, "givenName");
            mail = getString(credential, "mail");
        } catch (SamlObjectException e) {
            throw new UsernameNotFoundException("Failed to get all attributes", e);
        }
        log.debug("user details are oid {} firstname {} surname {} mail {}", oid, firstname, surname, mail);
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return new User(mail, "", true, true, true, true, authorities);
    }

    protected String getString(SAMLCredential credential, String attribute) throws SamlObjectException {
        return getSimpleValue(credential, attribute)
                .getStringValue();
    }

    protected Long getLong(SAMLCredential credential, String attribute) throws SamlObjectException {
        return getSimpleValue(credential, attribute)
                .getLongValue();
    }

    private SimpleValue getSimpleValue(SAMLCredential credential, String attribute) throws SamlObjectException {
        final Attribute attr = credential.getAttribute(attribute);
        if (attr == null) {
            throw new SamlObjectException("Attribute is empty");
        }
        if (attr.getAttributeValues() == null) {
            throw new SamlObjectException("Attribute '" + attr + "' has empty value");
        }
        if (attr.getAttributeValues().size() == 0) {
            throw new SamlObjectException("Attribute '" + attr + "' has empty value");
        }
        return (SimpleValue) attr.getAttributeValues().get(0);
    }
}
