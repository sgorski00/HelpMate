package pl.sgorski.user_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTests {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldHandleLogin() throws Exception {
        mockMvc.perform(get("/api/auth/callback")
                        .with(jwt().jwt(jwt -> jwt.tokenValue("testjwt"))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertEquals("testjwt", responseBody);
                });

        verify(userService, times(1)).crateUserIfNotExists(any());
    }

    @Test
    void shouldNotHandleLogin_JwtException() throws Exception {
        doThrow(new JwtException("JWT Exception")).when(userService).crateUserIfNotExists(any());

        mockMvc.perform(get("/api/auth/callback")
                        .with(jwt().jwt(jwt -> jwt.tokenValue("testjwt"))))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertTrue(responseBody.contains("JWT Exception"));
                });

        verify(userService, times(1)).crateUserIfNotExists(any());
    }
}
