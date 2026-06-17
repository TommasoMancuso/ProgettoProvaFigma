package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import da_figma.services.EventService;
import da_figma.services.TeamUserService;
import da_figma.services.CircuitService;

@Controller
public class UserController {

    @Autowired
    private TeamUserService teamUserService; 

    @Autowired
    private EventService eventService;

    @Autowired
    private CircuitService circuitService;

    // =========================
    // PUBLIC PAGES
    // =========================

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("circuits", circuitService.getActiveCircuits());
        return "home";
    }

    @GetMapping("/services")
    public String services(Model model) {

        model.addAttribute("circuits", circuitService.getActiveCircuits());
        return "services";
    }

    @GetMapping("/aboutus")
    public String aboutUs(Model model) {

        model.addAttribute("users", teamUserService.getAllUsers());

        return "aboutus";
    }

    @GetMapping("/events")
    public String events(Model model){

        model.addAttribute("events", eventService.getAllEvents());

        return "events";
    }

}