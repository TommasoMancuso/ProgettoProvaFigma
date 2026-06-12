package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import da_figma.dtos.BikeDto;
import da_figma.enums.BikeCategory;
import da_figma.enums.BikeStatus;
import da_figma.models.Bike;
import da_figma.services.BikeService;
import da_figma.repositories.BikeRepository;
import jakarta.validation.Valid;

@Controller
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @Autowired
    private BikeRepository bikeRepository;

    @GetMapping("/admin/bikes")
    public String adminMoto(Model model){

        model.addAttribute("bikes", bikeRepository.findByStatusNot(BikeStatus.ARCHIVED));

        return "admin/bikes/bikes";
    }

    @GetMapping("/admin/bikes/newBike")
    public String newBike(Model model){

        model.addAttribute("bike", new BikeDto());
        model.addAttribute("categories", BikeCategory.values());

        return "admin/bikes/newBike";
    }

    // ROTTA CREAZIONE MOTO
    @PostMapping("/admin/bikes/createBike")
    public String createBike(
            @Valid @ModelAttribute("bike") BikeDto bikeDto,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("categories", BikeCategory.values());
            model.addAttribute("bike", bikeDto);
            return "admin/bikes/newBike";
        }

        bikeService.createBike(bikeDto, imageFile);

        return "redirect:/admin/bikes";
    }

    // ROTTA PAGINA DI DETTAGLIO
    @GetMapping("/admin/bikes/detail/{id}")
    public String bikeDetail(@PathVariable Long id, Model model){

        Bike bike = bikeService.getBikeById(id);

        model.addAttribute("bike", bike);

        return "admin/bikes/bikeDetail";
    }

    // ROTTA FORM DI MODIFICA
   @GetMapping("/admin/bikes/edit/{id}")
    public String bikeEdit(@PathVariable Long id, Model model){

        Bike bike = bikeService.getBikeById(id);

        model.addAttribute("bike", bike);
        model.addAttribute("categories", BikeCategory.values());

        return "admin/bikes/bikeEdit";
    }

    // ROTTA PER SALVATAGGIO MODIFICHE
    @PostMapping("/admin/bikes/update/{id}")
    public String updateBike(
            @PathVariable Long id,
            @Valid @ModelAttribute("bike") BikeDto bikeDto,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("categories", BikeCategory.values());
            return "admin/bikes/bikeEdit";
        }

        bikeService.updateBike(id, bikeDto, imageFile);

        return "redirect:/admin/bikes";
    }

    // ROTTA SOFT DELETE
    @GetMapping("/admin/bikes/delete/{id}")
    public String deleteBike(@PathVariable Long id) {

        bikeService.archiveBike(id);

        return "redirect:/admin/bikes";
    }
}
