package pl.sgorski.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pl.sgorski.common.exception.UserNotFoundException;
import pl.sgorski.user_service.model.Role;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtDecodeService jwtDecodeService;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public void crateUserIfNotExists(Jwt jwt) {
        userRepository.findById(UUID.fromString(jwt.getSubject()))
                .ifPresentOrElse(u -> {
                    log.debug("User already exists: {}, checking for new roles", u.getUsername());
                    Set<Role> rolesFromJwt = roleService.mapToRoles(jwtDecodeService.getRolesNames(jwt));
                    if(roleService.hasRolesChanged(u.getRoles(), rolesFromJwt)) {
                        log.debug("User roles changed, updating user: {}, old roles: {}", u.getUsername(), u.getRoles());
                        u.setRoles(rolesFromJwt);
                        log.debug("New roles for user {}: {}", u.getUsername(), u.getRoles());
                        userRepository.save(u);
                    } else {
                        log.debug("User roles are the same, no update needed for: {}", u.getUsername());
                    }
                }, () -> {
                    User user = jwtDecodeService.getUser(jwt);
                    userRepository.save(user);
                    log.debug("New user created: {}", user.getUsername());
                    log.debug("User roles: {}", user.getRoles());
                });
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User: " + username + " not found"));
    }

    public Page<User> findAll(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }
}
