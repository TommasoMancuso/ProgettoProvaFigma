package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // =========================
    // PUBLIC PAGES
    // =========================

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/aboutus")
    public String aboutUs() {
        return "aboutus";
    }

    // =========================
    // AUTH PAGES (KEYCLOAK HANDLED)
    // =========================

    @GetMapping("/login")
    public String login() {
        // opzionale: redirect verso Keycloak login
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        // opzionale: pagina che reindirizza a Keycloak registration
        return "auth/register";
    }
}