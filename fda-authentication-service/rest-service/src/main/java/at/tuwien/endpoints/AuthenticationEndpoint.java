package at.tuwien.endpoints;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationEndpoint {

    @RequestMapping("/")
    public String index(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
        String givenName = principal.getFirstAttribute("givenName");
        String surName = principal.getFirstAttribute("sn");
        String oid = principal.getFirstAttribute("oid");
        String mail = principal.getFirstAttribute("mail");
        log.info("Successful login for {}", oid);
        log.debug("givenName {}, surname {}, oid {}, mail {}", givenName, surName, oid, mail);
        return "logged-in";
    }

}