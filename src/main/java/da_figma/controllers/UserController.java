package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import da_figma.services.TeamUserService;

@Controller
public class UserController {

    @Autowired
    private TeamUserService teamUserService; 

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
    public String aboutUs(Model model) {

        model.addAttribute("users", teamUserService.getAllUsers());

        return "aboutus";
    }

}