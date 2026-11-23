package da_figma.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.TeamUser;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    List<TeamUser> findByNameContainingIgnoreCase(String name);

    List<TeamUser> findBySurnameContainingIgnoreCase(String surname);

    List<TeamUser> findByTeamRole_Name(String name);

    Optional<TeamUser> findByEmail(String email);

    Optional<TeamUser> findByPhone(String phone);

    boolean existsByTeamRole_Name(String name);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}