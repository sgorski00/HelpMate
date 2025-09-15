package pl.sgorski.user_service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTests {

    @Test
    void shouldReturnRoleNameInsUpperCase() {
        Role role = new Role("admin");

        String result = role.getName();

        assertEquals(result, "ADMIN");
    }
}
