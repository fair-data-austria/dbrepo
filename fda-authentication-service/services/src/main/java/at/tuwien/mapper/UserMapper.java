package at.tuwien.mapper;

import at.tuwien.api.auth.JwtResponseDto;
import at.tuwien.api.auth.SignupRequestDto;
import at.tuwien.api.user.GrantedAuthorityDto;
import at.tuwien.api.user.UserDetailsDto;
import at.tuwien.api.user.UserDto;
import at.tuwien.entities.user.RoleType;
import at.tuwien.entities.user.User;
import org.mapstruct.Mapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserMapper.class);

    User signupRequestDtoToUser(SignupRequestDto data);

    UserDetailsDto userToUserDetailsDto(User data);

    default JwtResponseDto principalToJwtResponseDto(Object data) {
        final UserDetailsDto details = (UserDetailsDto) data;
        return JwtResponseDto.builder()
                .id(details.getId())
                .username(details.getUsername())
                .email(details.getEmail())
                .roles(details.getAuthorities()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();
    }

    default UserDto userToUserDto(User data) {
        return UserDto.builder()
                .id(data.getId())
                .username(data.getUsername())
                .email(data.getEmail())
                .password(data.getPassword())
                .authorities(data.getRoles()
                        .stream()
                        .map(this::roleTypeToGrantedAuthorityDto)
                        .collect(Collectors.toList()))
                .build();
    }

    default GrantedAuthority roleTypeToGrantedAuthority(RoleType data) {
        return new SimpleGrantedAuthority(data.name());
    }

    default GrantedAuthorityDto roleTypeToGrantedAuthorityDto(RoleType data) {
        return GrantedAuthorityDto.builder()
                .authority(data.name())
                .build();
    }

    default GrantedAuthorityDto grantedAuthorityToGrantedAuthority(GrantedAuthority data) {
        return GrantedAuthorityDto.builder()
                .authority(data.getAuthority())
                .build();
    }

    default UserDto userDetailsToUserDto(UserDetails data, Principal principal) {
        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        final UserDto user = UserDto.builder()
                .username(data.getUsername())
                .password(data.getPassword())
                .authorities(token.getAuthorities()
                        .stream()
                        .map(this::grantedAuthorityToGrantedAuthority)
                        .collect(Collectors.toList()))
                .build();
        log.debug("mapped user and principal {}", user);
        return user;
    }

}
