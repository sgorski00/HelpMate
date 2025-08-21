package pl.sgorski.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.user_service.model.Role;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @InjectMocks
    private RoleService roleService;

    @Test
    void hasRolesChanged_shouldReturnTrue_RolesChanged() {
        Set<Role> dbRoles = Set.of(new Role("USER"), new Role("ADMIN"));
        Set<Role> jwtRoles = Set.of(new Role("USER"), new Role("TECHNICIAN"));

        boolean res = roleService.hasRolesChanged(dbRoles, jwtRoles);

        assertTrue(res);
    }

    @Test
    void hasRolesChanged_shouldReturnFalse_SameRoles() {
        Set<Role> dbRoles = Set.of(new Role("USER"), new Role("ADMIN"));
        Set<Role> jwtRoles = Set.of(new Role("USER"), new Role("ADMIN"));

        boolean res = roleService.hasRolesChanged(dbRoles, jwtRoles);

        assertFalse(res);
    }

    @Test
    void hasRolesChanged_shouldReturnFalse_BothNull() {
        boolean res = roleService.hasRolesChanged(null, null);

        assertFalse(res);
    }

    @Test
    void hasRolesChanged_shouldReturnFalse_BothEmpty() {
        boolean res = roleService.hasRolesChanged(new HashSet<>(), new HashSet<>());

        assertFalse(res);
    }

    @Test
    void hasRolesChanged_shouldReturnFalse_OneNullOneEmpty() {
        boolean res = roleService.hasRolesChanged(null, new HashSet<>());

        assertFalse(res);
    }

    @Test
    void hasRolesChanged_shouldReturnTrue_OneFilledOneEmpty() {
        Set<Role> jwtRoles = Set.of(new Role("USER"), new Role("TECHNICIAN"));

        boolean res = roleService.hasRolesChanged(new HashSet<>(), jwtRoles);

        assertTrue(res);
    }

    @Test
    void shouldMapStringsToRoles() {
        Set<String> roleNames = Set.of("USER", "ADMIN", "TECHNICIAN");

        Set<Role> roles = roleService.mapToRoles(roleNames);

        assertTrue(roles.contains(new Role("USER")));
        assertTrue(roles.contains(new Role("ADMIN")));
        assertTrue(roles.contains(new Role("TECHNICIAN")));
        assertEquals(3, roles.size());
    }
}
