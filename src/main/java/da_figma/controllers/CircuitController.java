package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import da_figma.models.Circuit;
import da_figma.services.CircuitService;



@Controller
public class CircuitController {

    @Autowired
    private CircuitService circuitService;

    @GetMapping("/admin/circuits")
    public String circuits(Model model) {

        model.addAttribute(
                "circuits",
                circuitService.getActiveCircuits());

        return "admin/circuits/circuits";
    }

    @GetMapping("/admin/circuits/newCircuit")
    public String newCircuit(Model model) {

        model.addAttribute("circuit", new Circuit());

        return "admin/circuits/newCircuit";
    }

    @PostMapping("/admin/circuits/createCircuit")
    public String createCircuit(
            @Valid @ModelAttribute Circuit circuit,
            BindingResult bindingResult, MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            return "admin/circuits/newCircuit";
        }

        circuitService.createCircuit(circuit, imageFile);

        return "redirect:/admin/circuits";
    }

    @GetMapping("/admin/circuits/detail/{id}")
    public String circuitDetail(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "circuit",
                circuitService.getCircuitById(id));

        return "admin/circuits/circuitDetail";
    }

    @GetMapping("/admin/circuits/edit/{id}")
    public String editCircuit(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "circuit",
                circuitService.getCircuitById(id));

        return "admin/circuits/circuitEdit";
    }

    @PostMapping("/admin/circuits/update/{id}")
    public String updateCircuit(
            @PathVariable Long id,
            @Valid @ModelAttribute Circuit circuit,
            BindingResult bindingResult,
            MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            return "admin/circuits/circuitEdit";
        }

        circuitService.updateCircuit(id, circuit, imageFile);

        return "redirect:/admin/circuits";
    }

    @GetMapping("/admin/circuits/delete/{id}")
    public String deleteCircuit(@PathVariable Long id) {

        circuitService.archiveCircuit(id);

        return "redirect:/admin/circuits";
    }
}
