package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import da_figma.dtos.EventDto;
import da_figma.enums.EventStatus;
import da_figma.models.Event;
import da_figma.services.CircuitService;
import da_figma.services.EventService;
import da_figma.services.TeamServiceService;
import jakarta.validation.Valid;


@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private CircuitService circuitService;

    @Autowired
    private TeamServiceService teamServiceService;

    // LISTA
    @GetMapping("/admin/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/events/events";
    }

    // NEW FORM
    @GetMapping("/admin/events/newEvent")
    public String newEvent(Model model) {
        model.addAttribute("event", new EventDto());
        model.addAttribute("circuits", circuitService.getActiveCircuits());
        model.addAttribute("statuses", EventStatus.values());
        model.addAttribute("services", teamServiceService.getActiveServices());
        return "admin/events/newEvent";
    }

    // CREATE
    @PostMapping("/admin/events/create")
    public String createEvent(@ModelAttribute("event") EventDto dto) {

        Event event = eventService.createEvent(dto);

        return "redirect:/admin/events/" + event.getId() + "/prices";
    }

    // DETAIL (ENTITY OK)
    @GetMapping("/admin/events/detail/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "admin/events/eventDetail";
    }

    // EDIT FORM (DTO!)
    @GetMapping("/admin/events/edit/{id}")
    public String editEvent(@PathVariable Long id, Model model) {

        Event event = eventService.getEventById(id);

        EventDto dto = new EventDto();
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setOrganizer(event.getOrganizer());
        dto.setCircuitId(event.getCircuit().getId());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setMaxParticipants(event.getMaxParticipants());
        dto.setStatus(event.getStatus());
        dto.setServiceIds(event.getServicePrices().stream()
                                .map(esp -> esp.getService().getId()).toList());

        model.addAttribute("event", dto);
        model.addAttribute("id", id);
        model.addAttribute("circuits", circuitService.getActiveCircuits());
        model.addAttribute("statuses", EventStatus.values());
        model.addAttribute("services", teamServiceService.getActiveServices());

        return "admin/events/eventEdit";
    }

    // UPDATE
    @PostMapping("/admin/events/update/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") EventDto eventDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("circuits", circuitService.getActiveCircuits());
            model.addAttribute("statuses", EventStatus.values());
            model.addAttribute("id", id);
            model.addAttribute("services", teamServiceService.getActiveServices());
            return "admin/events/eventEdit";
        }

        eventService.updateEvent(id, eventDto);

        return "redirect:/admin/events/" + id + "/prices";
    }

    // DELETE
    @GetMapping("/admin/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.archiveEvent(id);
        return "redirect:/admin/events";
    }
}