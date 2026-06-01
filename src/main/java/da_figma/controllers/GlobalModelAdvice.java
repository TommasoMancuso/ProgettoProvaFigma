package da_figma.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import da_figma.services.UserContextService;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserContextService userContextService;

    public GlobalModelAdvice(UserContextService userContextService) {
        this.userContextService = userContextService;
    }

    // =========================
    // IDENTITÀ UTENTE (KEYCLOAK)
    // =========================
    @ModelAttribute("keycloakId")
    public String keycloakId() {
        try {
            return userContextService.getCurrentKeycloakId();
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // EMAIL UTENTE (OPZIONALE)
    // =========================
    @ModelAttribute("email")
    public String email() {
        try {
            return userContextService.getCurrentEmail();
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // CHECK LOGIN
    // =========================
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        return userContextService.isAuthenticated();
    }
}