package pl.sgorski.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import pl.sgorski.user_service.exception.UserNotFoundException;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private JwtDecodeService jwtDecodeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserIfNotExists() {
        when(jwt.getSubject()).thenReturn("test-user-id");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(jwtDecodeService.getUser(any())).thenReturn(new User());

        userService.crateUserIfNotExists(jwt);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotCreateUserIfExists() {
        when(jwt.getSubject()).thenReturn("test-user-id");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));

        userService.crateUserIfNotExists(jwt);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("testuser");

        assertEquals("testuser", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("abcd"));

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void shouldFindAllUsers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(userRepository.findAll(pageRequest)).thenReturn(Page.empty());

        var usersPage = userService.findAll(pageRequest);

        assertEquals(0, usersPage.getTotalElements());
        verify(userRepository, times(1)).findAll(pageRequest);
    }
}
