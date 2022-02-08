package at.tuwien.api.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @NotNull
    @ApiModelProperty(name = "user name")
    private String username;

    @NotNull
    @Email
    @ApiModelProperty(name = "user email")
    private String email;

    @NotNull
    @ApiModelProperty(name = "password hash")
    private String password;

}
