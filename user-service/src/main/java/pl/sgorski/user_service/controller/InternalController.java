package pl.sgorski.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.service.UserService;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable String id
    ) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
