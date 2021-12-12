package at.tuwien.service.impl;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.xmlbeans.SimpleValue;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.schema.XSString;
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

@Log4j2
@Service
public class AuthenticationServiceImpl implements SAMLUserDetailsService {

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        final String id = credential.getNameID().getValue();
        log.debug("user detail id {}", id);
        printAttributes(credential);
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
            log.info("Name {}, Value {}", name, credential.getAttributeAsString(name));
        }
    }

    @SneakyThrows
    protected void printAttributes(SAMLCredential credential) {
        final String oid = ((XSString) credential.getAttribute("urn:oid:0.9.2342.19200300.100.1.1").getAttributeValues().get(0)).getValue();
        final String lastname = ((XSString) credential.getAttribute("urn:oid:2.5.4.4").getAttributeValues().get(0)).getValue();
        final String firstname = ((XSString) credential.getAttribute("urn:oid:2.5.4.42").getAttributeValues().get(0)).getValue();
        final String mail = ((XSString) credential.getAttribute("urn:oid:0.9.2342.19200300.100.1.3").getAttributeValues().get(0)).getValue();
        log.debug("====> INFO: oid {}, lastname {}, firstname {}, mail {}", oid, lastname, firstname, mail);
    }
}
