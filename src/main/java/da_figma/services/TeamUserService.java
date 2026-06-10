package da_figma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${supabase.public-url}")
    private String supabasePublicUrl;

    public TeamUserService(TeamUserRepository userRepository,
                           TeamRoleRepository roleRepository,
                           SupabaseStorageService storageService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storageService = storageService;
    }

    // =========================
    // VALIDAZIONE CREATE
    // =========================
    private void validateUser(TeamUser user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("Numero di telefono già registrato");
        }
    }

    // =========================
    // VALIDAZIONE UPDATE
    // =========================
    private void validateUserUpdate(TeamUser existing, TeamUser updated) {

        if (!existing.getEmail().equals(updated.getEmail())
                && userRepository.existsByEmail(updated.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }

        if (!existing.getPhone().equals(updated.getPhone())
                && userRepository.existsByPhone(updated.getPhone())) {
            throw new RuntimeException("Numero di telefono già registrato");
        }
    }

    // =========================
    // CREATE
    // =========================
    @Transactional
    public TeamUser createTeamUser(TeamUser user,
                                   Long roleId,
                                   MultipartFile imageFile) {

        validateUser(user);

        user.setTeamRole(
                roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Ruolo non trovato"))
        );

        user.setProfileImage(resolveProfileImage(imageFile));

        return userRepository.save(user);
    }

    // =========================
    // UPDATE (QUESTO È QUELLO GIUSTO)
    // =========================
    @Transactional
    public TeamUser updateTeamUser(Long id,
                                   TeamUser updated,
                                   MultipartFile imageFile) {

        TeamUser existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamUser non trovato"));

        validateUserUpdate(existing, updated);

        existing.setName(updated.getName());
        existing.setSurname(updated.getSurname());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setBirthDate(updated.getBirthDate());
        existing.setBio(updated.getBio());
        existing.setInstagram(updated.getInstagram());

        // ruolo aggiornabile direttamente da entity (se presente)
        existing.setTeamRole(updated.getTeamRole());

        // immagine opzionale
        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setProfileImage(storageService.uploadFile(imageFile));
        }

        return userRepository.save(existing);
    }

    // =========================
    // DELETE
    // =========================
    @Transactional
    public void deleteTeamUser(Long id) {

        TeamUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamUser non trovato"));

        // 1. elimino immagine da Supabase (se esiste)
        if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
            storageService.deleteFile(user.getProfileImage());
        }

        // 2. elimino record DB
        userRepository.delete(user);
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

    public TeamUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public TeamUser getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public List<TeamUser> getUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public List<TeamUser> getUsersBySurname(String surname) {
        return userRepository.findBySurnameContainingIgnoreCase(surname);
    }

    // =========================
    // IMAGE DEFAULT LOGIC
    // =========================
    private String resolveProfileImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            return storageService.uploadFile(file);
        }

        return supabasePublicUrl + "/default.png";
    }
}