package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // Rotta get per la homepage
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // Rotta get per i nostri servizi
    @GetMapping("/services")
    public String servicesIndex(){
        return "services";
    }

    // Rotta Chi siamo
    @GetMapping("/aboutus")
    public String aboutUsIndex(){
        return "aboutus";
    }
}
