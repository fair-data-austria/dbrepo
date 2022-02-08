package at.tuwien.api.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto implements UserDetails {

    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "user authorities")
    private List<? extends GrantedAuthority> authorities;

    @NotNull
    @ApiModelProperty(name = "user name")
    private String username;

    @NotNull
    @ToString.Exclude
    @ApiModelProperty(name = "password hash")
    private String password;

    @NotNull
    @ApiModelProperty(name = "mail address")
    private String email;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
