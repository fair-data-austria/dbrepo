package at.tuwien.gateway.impl;

import at.tuwien.api.user.UserDto;
import at.tuwien.gateway.AuthenticationServiceGateway;
import at.tuwien.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationServiceGatewayImpl implements AuthenticationServiceGateway {

    private final UserMapper userMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthenticationServiceGatewayImpl(UserMapper userMapper, RestTemplate restTemplate) {
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails validate(String token) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        final ResponseEntity<UserDto> response = restTemplate.exchange("/api/auth", HttpMethod.PUT,
                new HttpEntity<>("", headers), UserDto.class);
        return userMapper.userDtoToUserDetailsDto(response.getBody());
    }

}
