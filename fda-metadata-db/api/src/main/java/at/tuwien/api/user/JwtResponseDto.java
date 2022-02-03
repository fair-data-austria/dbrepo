package at.tuwien.api.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {

    @NotNull
    @ApiModelProperty(name = "jwt")
    private String token;

    @NotNull
    @ApiModelProperty(name = "user type")
    private String type;

    @NotNull
    @ApiModelProperty(name = "id")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "user name")
    private String username;

    @NotNull
    @ApiModelProperty(name = "user email")
    private String email;

    @NotNull
    @ApiModelProperty(name = "user roles")
    private List<String> roles;

}
