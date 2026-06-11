package da_figma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BikeController {

    @GetMapping("/admin/bikes")
    public String adminMoto(){
        return "admin/bikes/bikes";
    }

    @GetMapping("admin/bikes/newBike")
    public String newBike(){
        return "admin/bikes/newBike";
    }

}
