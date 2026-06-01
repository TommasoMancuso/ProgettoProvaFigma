package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.TeamUser;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    List<TeamUser> findByNameContainingIgnoreCase(String name);

    List<TeamUser> findBySurnameContainingIgnoreCase(String surname);

    List<TeamUser> findByTeamRole_Name(String name);

    boolean existsByTeamRole_Name(String name);

}