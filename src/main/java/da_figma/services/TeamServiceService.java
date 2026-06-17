package da_figma.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.models.TeamService;
import da_figma.repositories.TeamServiceRepository;

@Service
public class TeamServiceService {

    private final TeamServiceRepository repository;

    public TeamServiceService(TeamServiceRepository repository) {
        this.repository = repository;
    }

    // =========================
    // CREATE
    // =========================
    @Transactional
    public TeamService createService(TeamService service) {

        validateService(service);

        service.setActive(true);

        return repository.save(service);
    }

    // =========================
    // UPDATE
    // =========================
    @Transactional
    public TeamService updateService(Long id, TeamService updated) {

        TeamService existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servizio non trovato"));

        validateServiceUpdate(existing, updated);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPricingType(updated.getPricingType());
        existing.setBasePrice(updated.getBasePrice());
        existing.setActive(updated.isActive());

        return repository.save(existing);
    }

    // =========================
    // SOFT DELETE
    // =========================
    @Transactional
    public void deleteService(Long id) {

        TeamService service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servizio non trovato"));

        service.setActive(false);

        repository.save(service);
    }

    // =========================
    // READ
    // =========================
    public TeamService getServiceById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servizio non trovato"));
    }

    public List<TeamService> getAllServices() {

        return repository.findAll();
    }

    public List<TeamService> getActiveServices() {

        return repository.findByActiveTrue();
    }

    // =========================
    // VALIDATION
    // =========================
    private void validateService(TeamService service) {

        if (service.getName() == null || service.getName().isBlank()) {
            throw new RuntimeException("Nome servizio obbligatorio");
        }
    }

    private void validateServiceUpdate(TeamService existing, TeamService updated) {

        if (updated.getName() == null || updated.getName().isBlank()) {
            throw new RuntimeException("Nome servizio obbligatorio");
        }
    }
}