package pl.sgorski.user_service.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.common.utils.JwtUtils;
import pl.sgorski.user_service.config.SecurityConfig;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.user_service.exception.UserNotFoundException;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.service.JwtDecodeService;
import pl.sgorski.user_service.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTests {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecodeService jwtDecodeService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                "testuser",
                "test@user.com",
                "John",
                "Doe"
        );
    }

    @Test
    void shouldReturnLoggedUser() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);
        try (MockedStatic<?> mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.getUsername(any())).thenReturn("testuser");

            mockMvc.perform(get("/api/users/me")
                            .with(jwt().jwt(jwt -> jwt.tokenValue("testjwt"))))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        String responseBody = result.getResponse().getContentAsString();
                        assertThat(responseBody).contains("testuser");
                        assertThat(responseBody).contains("test@user.com");
                        assertThat(responseBody).contains("John");
                        assertThat(responseBody).contains("Doe");
                    });
        }
    }

    @Test
    void shouldReturnLoggedUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUserByUsername() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(new User());
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/users/testuser")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("testuser");
                    assertThat(responseBody).contains("test@user.com");
                    assertThat(responseBody).contains("John");
                    assertThat(responseBody).contains("Doe");
                });
    }

    @Test
    void shouldNotReturnUserByUsername_UserNotFound() throws Exception {
        when(userService.getUserByUsername("testuser")).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/testuser")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("User not found");
                });
    }

    @Test
    void shouldNotReturnUserByUsername_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users/testuser")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotReturnUserByUsername_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUsersPage_DefaultValues() throws Exception {
        when(userService.findAll(any())).thenReturn(new PageImpl<>(List.of(new User())));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("testuser");
                    assertThat(responseBody).contains("test@user.com");
                    assertThat(responseBody).contains("John");
                    assertThat(responseBody).contains("Doe");
                });
    }

    @Test
    void shouldReturnUsersPage_CustomValues() throws Exception {
        when(userService.findAll(any())).thenReturn(Page.empty());
        when(userMapper.toDto(nullable(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUsersPage_WrongValues() throws Exception {
        when(userService.findAll(any())).thenReturn(Page.empty());
        when(userMapper.toDto(nullable(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .param("page", "notanumber")
                        .param("size", "heretoo"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldNotReturnUsersPage_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotReturnUsersPage_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
