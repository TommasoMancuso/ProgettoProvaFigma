package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.TeamService;

@Repository
public interface TeamServiceRepository extends JpaRepository<TeamService, Long> {

    List<TeamService> findByNameContainingIgnoreCase(String name);

    List<TeamService> findByActiveTrue();
}
