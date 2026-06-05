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

}