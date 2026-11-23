package da_figma.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.TeamRole;

@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRole, Long> {

    Optional<TeamRole> findByName(String name);

    boolean existsByName(String name);

   
}