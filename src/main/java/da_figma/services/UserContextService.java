package da_figma.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    private Authentication getAuthentication() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentKeycloakId() {

        Authentication auth = getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof OidcUser oidcUser) {

            return oidcUser.getSubject();
        }

        throw new RuntimeException("Utente non autenticato");
    }

    public String getCurrentEmail() {

        Authentication auth = getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof OidcUser oidcUser) {

            return oidcUser.getEmail();
        }

        return null;
    }

    public boolean isAuthenticated() {

        Authentication auth = getAuthentication();

        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);
    }
}