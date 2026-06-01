package da_figma.services;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class KeycloakJwtService {

    public String getKeycloakId(Jwt jwt) {
        return jwt.getSubject();
    }

    public String getEmail(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public String getFirstName(Jwt jwt) {
        return jwt.getClaimAsString("given_name");
    }

    public String getLastName(Jwt jwt) {
        return jwt.getClaimAsString("family_name");
    }

    public boolean isAdmin(Jwt jwt) {
        return jwt.getClaimAsStringList("realm_access.roles")
                  .contains("ADMIN");
    }
}