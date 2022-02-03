package at.tuwien.api.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotNull
    @ApiModelProperty(name = "user name")
    private String username;

    @NotNull
    @ApiModelProperty(name = "password hash")
    private String password;

}
