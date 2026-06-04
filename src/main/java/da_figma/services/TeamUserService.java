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

    public TeamUserService(TeamUserRepository userRepository,
                           TeamRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // =========================
    // CREATE STAFF (ADMIN ONLY)
    // =========================
    @Transactional
    public TeamUser createTeamUser(TeamUser user, MultipartFile imageFile) {

        // ruolo obbligatorio scelto dall'admin
        if (user.getTeamRole() == null) {
            throw new RuntimeException("Ruolo staff obbligatorio");
        }

        user.setProfileImage(resolveProfileImage(imageFile));

        return userRepository.save(user);
    }

    // =========================
    // UPDATE STAFF
    // =========================
    @Transactional
    public TeamUser updateTeamUser(Long id, TeamUser updated, MultipartFile imageFile) {

        TeamUser existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamUser non trovato"));

        existing.setName(updated.getName());
        existing.setSurname(updated.getSurname());
        existing.setPhone(updated.getPhone());
        existing.setBirthDate(updated.getBirthDate());
        existing.setBio(updated.getBio());
        existing.setInstagram(updated.getInstagram());
        existing.setTeamRole(updated.getTeamRole());

        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setProfileImage(uploadToStorage(imageFile));
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
    // IMMAGINI
    // =========================
    private String resolveProfileImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            return uploadToStorage(file);
        }

        return "/images/default.png";
    }

    private String uploadToStorage(MultipartFile file) {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // FUTURO: storage reale (Supabase / S3)
        return "https://YOUR_BUCKET_URL/" + fileName;
    }
}