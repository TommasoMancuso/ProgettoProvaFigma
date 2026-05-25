package da_figma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }

        Role roleToAssign;

        if (user.getRole() == null) {
            roleToAssign = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Ruolo USER non trovato"));
        } else {
            roleToAssign = roleRepository.findByName(user.getRole().getName())
                    .orElseThrow(() -> new RuntimeException("Ruolo non valido"));
        }

        user.setRole(roleToAssign);

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User non trovato"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchByName(String name) {
    return userRepository.findByNameContainingIgnoreCase(name);
    }

    public List<User> searchBySurname(String surname) {
        return userRepository.findBySurnameContainingIgnoreCase(surname);
    }

    public List<User> searchByRole(String roleName) {
        return userRepository.findByRole_Name(roleName);
    }
}
