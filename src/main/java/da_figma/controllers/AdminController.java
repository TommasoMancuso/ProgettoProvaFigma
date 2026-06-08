package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // STAFF
    @GetMapping("/admin/staff")
    public String adminStaff(){
        return "admin/staff";
    }

    // Rotta per pagina create staff
    @GetMapping("/newStaff")
    public String newStaff(){
        return "admin/newStaff";
    }

    // MOTO
    @GetMapping("/admin/bikes")
    public String adminMoto(){
        return "admin/bikes";
    }

}
