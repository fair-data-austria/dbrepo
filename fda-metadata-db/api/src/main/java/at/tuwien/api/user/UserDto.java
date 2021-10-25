package at.tuwien.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.security.auth.Subject;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.security.Principal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable, Principal {

    @XmlElement(name = "tissID")
    private String id;

    private String givenName;

    @XmlElement(name = "sn")
    private String surname;

    private String mail;

    @Override
    public String getName() {
        return givenName + " " + surname;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
