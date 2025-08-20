package pl.sgorski.user_service.service;

import org.springframework.stereotype.Service;
import pl.sgorski.user_service.model.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    public boolean hasRolesChanged(Set<Role> dbRoles, Set<Role> jwtRoles) {
        if (dbRoles == null || dbRoles.isEmpty()) {
            return !(jwtRoles == null || jwtRoles.isEmpty());
        }

        return !dbRoles.equals(jwtRoles);
    }

    public Set<Role> mapToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(Role::new)
                .collect(Collectors.toSet());
    }
}
