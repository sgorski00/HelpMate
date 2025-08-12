package pl.sgorski.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.service.JwtDecodeService;
import pl.sgorski.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtDecodeService jwtDecodeService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwtDecodeService.getUsername(jwt);
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable String username
    ) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(
                userService.findAll(pageRequest).map(userMapper::toDto)
        );
    }
}
