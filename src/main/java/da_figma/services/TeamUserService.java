package da_figma.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import da_figma.models.TeamUser;
import da_figma.repositories.TeamRoleRepository;
import da_figma.repositories.TeamUserRepository;

@Service
public class TeamUserService {

    private final TeamUserRepository userRepository;
    private final TeamRoleRepository roleRepository;
    private final SupabaseStorageService storageService;

    public TeamUserService(TeamUserRepository userRepository,
                           TeamRoleRepository roleRepository,
                           SupabaseStorageService storageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storageService = storageService;
    }

    // =========================
    // CREATE STAFF
    // =========================
    @Transactional
    public TeamUser createTeamUser(TeamUser user, Long roleId, MultipartFile imageFile) {

        // assegna ruolo
        user.setTeamRole(
                roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Ruolo non trovato"))
        );

        // immagine
        user.setProfileImage(resolveProfileImage(imageFile));

        return userRepository.save(user);
    }

    // =========================
    // UPDATE STAFF
    // =========================
    @Transactional
    public TeamUser updateTeamUser(Long id, TeamUser updated, Long roleId, MultipartFile imageFile) {

        TeamUser existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamUser non trovato"));

        existing.setName(updated.getName());
        existing.setSurname(updated.getSurname());
        existing.setPhone(updated.getPhone());
        existing.setBirthDate(updated.getBirthDate());
        existing.setBio(updated.getBio());
        existing.setInstagram(updated.getInstagram());

        existing.setTeamRole(
                roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Ruolo non trovato"))
        );

        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setProfileImage(storageService.uploadFile(imageFile));
        }

        return userRepository.save(existing);
    }

    // =========================
    // DELETE STAFF
    // =========================
    @Transactional
    public void deleteTeamUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("TeamUser non trovato");
        }

        userRepository.deleteById(id);
    }

    // =========================
    // READ
    // =========================
    public TeamUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamUser non trovato"));
    }

    public List<TeamUser> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================
    // IMAGE DEFAULT LOGIC
    // =========================
    private String resolveProfileImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            return storageService.uploadFile(file);
        }

        // fallback consigliato (nel bucket Supabase)
        return supabasePublicUrl() + "/default-profile.png";
    }

    private String supabasePublicUrl() {
        return "https://eyyrktpxarucykgzhaiy.supabase.co/storage/v1/object/public/daFigma";
    }
}