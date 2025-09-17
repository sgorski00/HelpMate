package pl.sgorski.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.user_service.config.SecurityConfig;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.service.JwtDecodeService;
import pl.sgorski.user_service.service.UserService;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalControllerTests {

    @MockitoBean
    private JwtDecodeService jwtDecodeService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                UUID.randomUUID(),
                "testuser",
                "test@user.com",
                "John",
                "Doe",
                Set.of("ROLE_USER")
        );
    }

    @Test
    @WithMockUser(roles = {"SERVICE_TICKET"})
    void shouldReturnUserById_StatusOK() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/internal/users/{id}", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains(userDto.email());
                    assertThat(responseBody).contains(userDto.firstname());
                    assertThat(responseBody).contains(userDto.lastname());
                    assertThat(responseBody).contains(userDto.id().toString());
                });
    }

    @Test
    @WithMockUser(roles = {"NOT_ALLOWED"})
    void shouldNotReturnUserById_Status403() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/internal/users/{id}", userDto.id()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"SERVICE_TICKET"})
    void shouldNotReturnUserById_Status404() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/internal/users/{id}", userDto.id()))
                .andExpect(status().isNotFound());
    }
}
