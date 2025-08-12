package pl.sgorski.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pl.sgorski.user_service.exception.UserNotFoundException;
import pl.sgorski.user_service.model.User;
import pl.sgorski.user_service.repository.UserRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtDecodeService jwtDecodeService;
    private final UserRepository userRepository;

    public void crateUserIfNotExists(Jwt jwt) {
        userRepository.findByUsername(jwtDecodeService.getUsername(jwt))
                .ifPresentOrElse(u -> {}, () -> {
                    User user = jwtDecodeService.getUser(jwt);
                    userRepository.save(user);
                    log.info("New user created: {}", user.getUsername());
                });
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User: " + username + " not found"));
    }

    public Page<User> findAll(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }
}
