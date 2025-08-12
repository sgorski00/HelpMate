package pl.sgorski.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/callback")
    public ResponseEntity<?> handleLogin(@AuthenticationPrincipal Jwt jwt) {
        userService.crateUserIfNotExists(jwt);
        return ResponseEntity.ok(jwt.getTokenValue());
    }
}
