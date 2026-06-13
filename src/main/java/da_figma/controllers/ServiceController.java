package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import da_figma.models.TeamService;
import da_figma.services.TeamServiceService;

@Controller
@RequestMapping("/admin/services")
public class ServiceController {

    private final TeamServiceService serviceService;

    public ServiceController(TeamServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // =========================
    // LISTA SERVIZI
    // =========================
    @GetMapping
    public String listServices(Model model) {

        model.addAttribute("services", serviceService.getAllServices());

        return "admin/services/services";
    }

    // =========================
    // FORM CREAZIONE
    // =========================
    @GetMapping("/new")
    public String newService(Model model) {

        model.addAttribute("service", new TeamService());

        return "admin/services/newService";
    }

    // =========================
    // CREAZIONE
    // =========================
    @PostMapping("/create")
    public String createService(@ModelAttribute TeamService service) {

        serviceService.createService(service);

        return "redirect:/admin/services";
    }

    // =========================
    // DETTAGLIO
    // =========================
    @GetMapping("/detail/{id}")
    public String detailService(@PathVariable Long id, Model model) {

        model.addAttribute("service", serviceService.getServiceById(id));

        return "admin/services/serviceDetail";
    }

    // =========================
    // FORM EDIT
    // =========================
    @GetMapping("/edit/{id}")
    public String editService(@PathVariable Long id, Model model) {

        model.addAttribute("service", serviceService.getServiceById(id));

        return "admin/services/editService";
    }

    // =========================
    // UPDATE
    // =========================
    @PostMapping("/update/{id}")
    public String updateService(@PathVariable Long id,
                                @ModelAttribute TeamService service) {

        serviceService.updateService(id, service);

        return "redirect:/admin/services";
    }

    // =========================
    // DELETE (SOFT)
    // =========================
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id) {

        serviceService.deleteService(id);

        return "redirect:/admin/services";
    }
}