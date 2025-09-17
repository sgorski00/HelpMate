package pl.sgorski.user_service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtDecodeServiceTests {

    @Mock
    private Validator validator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private JwtDecodeService jwtDecodeService;

    @Test
    void shouldMapToUserFromJwt() {
        String username = "testuser";
        String email = "testuser@gmail.com";
        String firstName = "John";
        String lastName = "Doe";
        String userId = UUID.randomUUID().toString();

        when(jwt.getSubject()).thenReturn(userId);
        when(validator.validate(any())).thenReturn(Set.of());
        when(userMapper.toUser(any())).thenReturn(new User(username, email, firstName, lastName));

        User user = jwtDecodeService.getUser(jwt);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(firstName, user.getFirstname());
        assertEquals(lastName, user.getLastname());
    }

    @Test
    void shouldThrowIfNotValidData() {
        //noinspection unchecked
        ConstraintViolation<UserDto> violation = mock(ConstraintViolation.class);
        when(jwt.getSubject()).thenReturn(UUID.randomUUID().toString());
        when(validator.validate(any(UserDto.class))).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> jwtDecodeService.getUser(jwt));

        verify(userMapper, never()).toUser(any(UserDto.class));
    }

    @Test
    void shouldGetRolesNamesFromJwt() {
        when(jwt.getClaim("realm_access")).thenReturn(Map.of("roles", List.of("ROLE_USER", "ROLE_ADMIN")));

        Set<String> roles = jwtDecodeService.getRolesNames(jwt);

        assertEquals(Set.of("ROLE_USER", "ROLE_ADMIN"), roles);
    }

    @Test
    void shouldReturnEmptyRolesIfClaimNotPresent() {
        when(jwt.getClaim("realm_access")).thenReturn(null);

        Set<String> roles = jwtDecodeService.getRolesNames(jwt);

        assertEquals(Set.of(), roles);
    }

    @Test
    void shouldReturnEmptyRolesIfEmptySet() {
        when(jwt.getClaim("realm_access")).thenReturn(Map.of());

        Set<String> roles = jwtDecodeService.getRolesNames(jwt);

        assertEquals(Set.of(), roles);
    }

    @Test
    void shouldReturnEmptyRolesIfNull() {
        when(jwt.getClaim("realm_access")).thenReturn(null);

        Set<String> roles = jwtDecodeService.getRolesNames(jwt);

        assertEquals(Set.of(), roles);
    }

    @Test
    void shouldReturnEmptyRolesIfNotList() {
        when(jwt.getClaim("realm_access")).thenReturn("roles", "not_a_list");

        Set<String> roles = jwtDecodeService.getRolesNames(jwt);

        assertEquals(Set.of(), roles);
    }
}
