package da_figma.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    // =========================
    // KEYCLOAK ID UTENTE LOGGATO
    // =========================
    public String getCurrentKeycloakId() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Utente non autenticato");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        return auth.getName();
    }

    // =========================
    // EMAIL (UTILE PER FUTURO BUSINESS / DEBUG)
    // =========================
    public String getCurrentEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }

        return null;
    }

    // =========================
    // CONTROLLO GENERICO LOGIN
    // =========================
    public boolean isAuthenticated() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.isAuthenticated();
    }
}