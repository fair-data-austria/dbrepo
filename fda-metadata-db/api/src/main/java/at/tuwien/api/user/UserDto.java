package at.tuwien.api.user;

import at.tuwien.api.container.ContainerDto;
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
    @ApiModelProperty(name = "titles before the first name")
    private String titlesBefore;

    @NotNull
    @ApiModelProperty(name = "titles after the last name")
    private String titlesAfter;

    @NotNull
    @ApiModelProperty(name = "first name")
    private String firstname;

    @NotNull
    @ApiModelProperty(name = "last name")
    private String lastname;

    @NotNull
    @ApiModelProperty(name = "list of containers")
    private List<ContainerDto> containers;

    @NotNull
    @ApiModelProperty(name = "list of databases")
    private List<ContainerDto> databases;

    @NotNull
    @ApiModelProperty(name = "list of identifiers")
    private List<ContainerDto> identifiers;

    @NotNull
    @ToString.Exclude
    @JsonIgnore
    @ApiModelProperty(name = "password hash")
    private String password;

    @NotNull
    @ApiModelProperty(name = "mail address")
    private String email;

}
