package pl.sgorski.user_service.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Log4j2
@Component
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public CustomJwtAuthenticationConverter() {
        super();
        this.setJwtGrantedAuthoritiesConverter(jwt -> {
            log.debug("Converting JWT realm roles to Granted Authorities");
            var authorities = new ArrayList<GrantedAuthority>();

            Object realmAccessObj = jwt.getClaim("realm_access");
            if(realmAccessObj instanceof Map<?,?> realmAccess && realmAccess.containsKey("roles")) {
                log.debug("Found realm_access section in JWT and it contains roles");
                Object rolesObj = realmAccess.get("roles");
                if(rolesObj instanceof Collection<?> roles) {
                    log.debug("Found roles in realm_access");
                    for (Object role : roles) {
                        log.debug("Adding role: {}", role);
                        if(role instanceof String r) authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                    }
                }
            }
            return authorities;
        });
    }
}
