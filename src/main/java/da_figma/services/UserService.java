package da_figma.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import da_figma.models.Role;
import da_figma.models.User;
import da_figma.repositories.RoleRepository;
import da_figma.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // CREATE USER
    // =========================
    @Transactional
    public User createUser(User user, MultipartFile imageFile) {

        // 1. controllo email unica
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }

        // 2. ruolo di default USER
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Ruolo USER non trovato"));

        user.setRole(role);

        // 3. immagine (default o upload)
        String imageUrl = resolveProfileImage(imageFile);
        user.setProfileImage(imageUrl);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. salva su DB
        User savedUser = userRepository.save(user);

        // 5. login automatico
        autoLogin(savedUser);

        return savedUser;
    }

    // =========================
    // IMMAGINE
    // =========================
    private String resolveProfileImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            return uploadToStorage(file);
        }

        // immagine default
        return "/images/default-user.png";
    }

    private String uploadToStorage(MultipartFile file) {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // QUI poi metterai Supabase vero
        return "https://YOUR_BUCKET_URL/" + fileName;
    }

    // =========================
    // LOGIN AUTOMATICO
    // =========================
    private void autoLogin(User user) {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // =========================
    // METODI ESISTENTI
    // =========================

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User non trovato"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}