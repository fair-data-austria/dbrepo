package at.tuwien.mapper;

import at.tuwien.api.user.SignupRequestDto;
import at.tuwien.api.user.UserDto;
import at.tuwien.entities.user.User;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User signupRequestDtoToUser(SignupRequestDto data);

    default UserDto userToUserDto(User data) {
        return UserDto.builder()
                .id(data.getId())
                .username(data.getUsername())
                .email(data.getEmail())
                .password(data.getPassword())
                .authorities(data.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()))
                .build();
    }

}
