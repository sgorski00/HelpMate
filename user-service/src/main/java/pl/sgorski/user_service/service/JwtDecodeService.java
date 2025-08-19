package pl.sgorski.user_service.service;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;

import java.util.Set;

import static pl.sgorski.common.utils.JwtUtils.*;

@Service
@RequiredArgsConstructor
public class JwtDecodeService {

    private final Validator validator;
    private final UserMapper userMapper;

    public User getUser(Jwt jwt) {
        UserDto user = new UserDto(
                getUsername(jwt),
                getEmail(jwt),
                getFirstName(jwt),
                getLastName(jwt)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        if(!violations.isEmpty()) {
            throw new IllegalArgumentException("User not valid: " + violations);
        }

        return userMapper.toUser(user);
    }
}
