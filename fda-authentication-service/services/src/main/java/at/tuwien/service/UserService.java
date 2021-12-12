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
        final String oid = credential.getAttributeAsString("oid");
        String surname;
        String firstname;
        String mail;
//        try {
//            oid = getLong(credential, "oid");
//            surname = getString(credential, "sn");
//            firstname = getString(credential, "givenName");
//            mail = getString(credential, "mail");
//        } catch (SamlObjectException e) {
//            throw new UsernameNotFoundException("Failed to get all attributes", e);
//        }
        log.debug("user detail id {}", oid);
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        return new User(oid, "", true, true, true, true, authorities);
    }
}
