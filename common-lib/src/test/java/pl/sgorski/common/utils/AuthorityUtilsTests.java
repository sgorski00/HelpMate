package pl.sgorski.common.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AuthorityUtilsTests {

    @Test
    void shouldReturnTrueForAdminRole() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isAdmin(auth);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForAdminRole_EmptyList() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of();

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isAdmin(auth);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForAdminRole_NotAdmin() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of(new SimpleGrantedAuthority("ROLE_TECHNICIAN"), new SimpleGrantedAuthority("ROLE_OTHER"));

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isAdmin(auth);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForAdminRole_NullCollection() {
        Authentication auth = mock(Authentication.class);

        doReturn(null).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isAdmin(auth);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueForTechnicianRole() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isTechnician(auth);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForTechnicianRole_EmptyList() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of();

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isTechnician(auth);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForTechnicianRole_NotTechnician() {
        Authentication auth = mock(Authentication.class);
        var authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_OTHER"));

        doReturn(authorities).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isTechnician(auth);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForTechnicianRole_NullCollection() {
        Authentication auth = mock(Authentication.class);

        doReturn(null).when(auth).getAuthorities();

        boolean result = AuthorityUtils.isTechnician(auth);

        assertFalse(result);
    }
}
