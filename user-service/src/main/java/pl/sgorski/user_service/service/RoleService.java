package pl.sgorski.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.sgorski.user_service.model.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    public boolean hasRolesChanged(Set<Role> dbRoles, Set<Role> jwtRoles) {
        if (CollectionUtils.isEmpty(dbRoles)) {
            return !CollectionUtils.isEmpty(jwtRoles);
        }

        return !dbRoles.equals(jwtRoles);
    }

    public Set<Role> mapToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(Role::new)
                .collect(Collectors.toSet());
    }
}
