package da_figma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import da_figma.models.TeamUser;
import da_figma.services.TeamRoleService;
import da_figma.services.TeamUserService;

@Controller
public class AdminController {

    @Autowired
    private TeamRoleService roleService;

    @Autowired
    private TeamUserService teamUserService;

    // STAFF
    @GetMapping("/admin/staff")
    public String adminStaff(Model model){

         model.addAttribute("users", teamUserService.getAllUsers());

        return "admin/staff";
    }

    // Rotta per pagina create staff
    @GetMapping("/newStaff")
    public String newStaff(Model model){
        
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("teamUser", new TeamUser());

        return "admin/newStaff";
    }

    // Rotta per creazione nuovo membro staff
    @PostMapping("/createStaff")
    public String createStaff(@ModelAttribute TeamUser user,
                            @RequestParam("roleId") Long roleId,
                            @RequestParam("imageFile") MultipartFile imageFile) {

        teamUserService.createTeamUser(user, roleId, imageFile);

        return "redirect:/admin/staff";
    }

    // Rotta pagina di dettaglio
    @GetMapping("/admin/staff/detail/{id}")
    public String staffDetail(@PathVariable Long id, Model model) {

        TeamUser user = teamUserService.getUserById(id);

        model.addAttribute("user", user);

        return "admin/staffDetail";
    }

    // MOTO
    @GetMapping("/admin/bikes")
    public String adminMoto(){
        return "admin/bikes";
    }

}
