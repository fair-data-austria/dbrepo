package at.tuwien.api.user;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String id;

    private String oId;

    private String firstname;

    private String surname;

    private String mail;

}
