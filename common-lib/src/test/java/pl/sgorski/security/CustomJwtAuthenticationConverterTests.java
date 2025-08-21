package pl.sgorski.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomJwtAuthenticationConverterTests {

    @InjectMocks
    private CustomJwtAuthenticationConverter converter;

    @Mock
    private Jwt jwt;

    @Test
    void convertsJwtWithValidRealmRolesToGrantedAuthorities() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("USER", "ADMIN"));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities)
                .hasSize(2)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void returnsEmptyAuthoritiesWhenRealmAccessIsMissing() {
        when(jwt.getClaim("realm_access")).thenReturn(null);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void returnsEmptyAuthoritiesWhenRolesAreMissingInRealmAccess() {
        Map<String, Object> realmAccess = Map.of();
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void returnsEmptyAuthoritiesWhenRolesAreNotACollection() {
        Map<String, Object> realmAccess = Map.of("roles", "INVALID");
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void ignoresNonStringRolesInRealmAccess() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("USER", 123, true));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        assertThat(authorities)
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }
}
