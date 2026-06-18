package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import da_figma.dtos.BookingRequestDto;
import da_figma.services.BookingFacadeService;
import da_figma.services.CircuitService;
import da_figma.services.EventService;
import da_figma.services.TeamUserService;

@Controller
public class UserController {

    @Autowired
    private TeamUserService teamUserService; 

    @Autowired
    private EventService eventService;

    @Autowired
    private CircuitService circuitService;

    @Autowired
    private BookingFacadeService bookingFacadeService;

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

    // ROTTA PER ENTRARE NELLA PAGINA DEGLI EVENTI
    @GetMapping("/events")
    public String events(Model model){

        model.addAttribute("events", eventService.getAllEvents());

        return "events";
    }

    // ROTTA PER PAGINA DI DETTAGLIO EVENTI
    @GetMapping("/user/eventDetail/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {

        model.addAttribute("event", eventService.getEventById(id));
        
        return "user/eventDetail";
    }

    // ROTTA PER FORM PRENOTAZIONE
    @GetMapping("/user/booking/{eventId}")
    public String bookingForm(@PathVariable Long eventId, Model model) {

        model.addAttribute("bookingForm", bookingFacadeService.buildBookingForm(eventId));

        model.addAttribute("bookingRequest", new BookingRequestDto());

        return "user/bookingForm";
    }

}