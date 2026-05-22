package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // Mappa get per la homepage
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
