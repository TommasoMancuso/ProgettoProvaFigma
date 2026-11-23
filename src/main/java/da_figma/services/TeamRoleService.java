package da_figma.services;

import java.util.List;

import org.springframework.stereotype.Service;

import da_figma.models.TeamRole;
import da_figma.repositories.TeamRoleRepository;

@Service
public class TeamRoleService {

    private final TeamRoleRepository teamRoleRepository;

    public TeamRoleService(TeamRoleRepository teamRoleRepository) {
        this.teamRoleRepository = teamRoleRepository;
    }

    public List<TeamRole> findAll() {
        return teamRoleRepository.findAll();
    }

    public TeamRole findById(Long id) {
        return teamRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
    }

}