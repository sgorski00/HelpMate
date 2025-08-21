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


import java.util.Set;

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
    void shouldMapToUser() {
        String username = "testuser";
        String email = "testuser@gmail.com";
        String firstName = "John";
        String lastName = "Doe";

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
        when(validator.validate(any(UserDto.class))).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> jwtDecodeService.getUser(jwt));

        verify(userMapper, never()).toUser(any(UserDto.class));
    }
}
