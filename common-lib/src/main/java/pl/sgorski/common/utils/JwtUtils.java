package pl.sgorski.common.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUtils {

    public static String getUsername(Jwt jwt) {
        return getClaim(jwt, "preferred_username");
    }

    public static String getEmail(Jwt jwt) {
        return getClaim(jwt, "email");
    }

    public static String getFirstName(Jwt jwt) {
        return getClaim(jwt, "given_name");
    }

    public static String getLastName(Jwt jwt) {
        return getClaim(jwt, "family_name");
    }

    private static String getClaim(Jwt jwt, String claimName) {
        return jwt.getClaimAsString(claimName);
    }
}
