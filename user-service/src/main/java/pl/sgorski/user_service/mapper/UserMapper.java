package pl.sgorski.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.user_service.model.Role;
import pl.sgorski.user_service.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoleNames")
    UserDto toDto(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoles")
    User toUser(UserDto userDto);

    @Named("toRoles")
    default Set<Role> toRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(Role::new)
                .collect(Collectors.toSet());
    }

    @Named("toRoleNames")
    default Set<String> toRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
