package at.tuwien.mapper;

import at.tuwien.api.user.UserDto;
import at.tuwien.entities.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({})
    public UserDto userToUserDto(User data);

}
