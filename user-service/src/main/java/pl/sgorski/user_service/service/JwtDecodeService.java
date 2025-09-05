package pl.sgorski.user_service.service;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pl.sgorski.common.dto.UserDto;
import pl.sgorski.user_service.mapper.UserMapper;
import pl.sgorski.user_service.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtDecodeService {

    private final Validator validator;
    private final UserMapper userMapper;

    public User getUser(Jwt jwt) {
        UserDto user = new UserDto(
                UUID.fromString(jwt.getSubject()),
                getUsername(jwt),
                getEmail(jwt),
                getFirstName(jwt),
                getLastName(jwt),
                getRolesNames(jwt)
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        if(!violations.isEmpty()) {
            throw new IllegalArgumentException("User not valid: " + violations);
        }

        return userMapper.toUser(user);
    }

    public Set<String> getRolesNames(Jwt jwt) {
        try {
            Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");
            if(realmAccess.get("roles") instanceof List<String> list) {
                return new HashSet<>(list);
            }else {
                throw new IllegalArgumentException("Invalid roles format in JWT");
            }
        } catch (Exception e) {
            return Set.of();
        }
    }

    public String getUsername(Jwt jwt) {
        return getClaim(jwt, "preferred_username");
    }

    public String getEmail(Jwt jwt) {
        return getClaim(jwt, "email");
    }

    public String getFirstName(Jwt jwt) {
        return getClaim(jwt, "given_name");
    }

    public String getLastName(Jwt jwt) {
        return getClaim(jwt, "family_name");
    }

    private String getClaim(Jwt jwt, String claimName) {
        return jwt.getClaimAsString(claimName);
    }
}
