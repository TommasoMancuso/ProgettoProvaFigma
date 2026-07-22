package da_figma.controllers;

import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import da_figma.dtos.EventPricingDto;
import da_figma.models.Event;
import da_figma.services.EventPricingService;
import da_figma.services.EventService;

@Controller
public class EventPricingController {

    @Autowired
    private EventPricingService eventPricingService;

    @Autowired
    private EventService eventService;

    @GetMapping("/admin/events/{eventId}/prices")
    public String configurePrices(@PathVariable Long eventId, Model model) {

        Event event = eventService.getEventById(eventId);

        long eventDays = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;

        model.addAttribute("pricing", eventPricingService.getPricingForm(eventId));

        model.addAttribute("event", event);

        model.addAttribute("eventDays", eventDays);

        return "admin/events/eventPricing";
    }

    @PostMapping("/admin/events/prices/save")
    public String savePrices(@ModelAttribute("pricing") EventPricingDto pricingDto) {

        eventPricingService.savePrices(pricingDto);

        return "redirect:/admin/events";
    }

    // ROTTA PER MODIFICA PREZZI
    @GetMapping("/admin/events/{eventId}/prices/edit")
    public String editPrices(@PathVariable Long eventId, Model model) {

        Event event = eventService.getEventById(eventId);

        long eventDays = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;

        model.addAttribute("pricing", eventPricingService.getPricingForm(eventId));

        model.addAttribute("event", event);

        model.addAttribute("eventDays", eventDays);

        return "admin/events/editPricing";
    }
}