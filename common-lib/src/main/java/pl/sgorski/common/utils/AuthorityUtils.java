package pl.sgorski.common.utils;

import org.springframework.security.core.Authentication;

public class AuthorityUtils {

    public static boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    public static boolean isTechnician(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_TECHNICIAN"));
    }
}
