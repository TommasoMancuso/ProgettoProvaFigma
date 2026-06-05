package da_figma.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentKeycloakId() {

        Authentication auth = getAuthentication();

        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        throw new RuntimeException("Utente non autenticato o JWT non valido");
    }

    public String getCurrentEmail() {

        Authentication auth = getAuthentication();

        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }

        return null;
    }

    public boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated();
    }
}