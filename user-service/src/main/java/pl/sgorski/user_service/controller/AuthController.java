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
        //this endpoint should be called after successful login
        //it will create user if not exists and update roles if they changed

        //TODO (optional): consider using filter after auth in securityfilterchain
        userService.crateUserIfNotExists(jwt);
        return ResponseEntity.ok(jwt.getTokenValue());
    }
}
