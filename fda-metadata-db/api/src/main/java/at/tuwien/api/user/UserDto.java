package at.tuwien.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserDto {

    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "user authorities")
    private List<GrantedAuthorityDto> authorities;

    @NotNull
    @ApiModelProperty(name = "user name")
    private String username;

    @NotNull
    @ToString.Exclude
    @JsonIgnore
    @ApiModelProperty(name = "password hash")
    private String password;

    @NotNull
    @ApiModelProperty(name = "mail address")
    private String email;

}
