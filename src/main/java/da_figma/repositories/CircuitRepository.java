package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.Circuit;

@Repository
public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    List<Circuit> findByNameContainingIgnoreCase(String name);

    List<Circuit> findByCountryContainingIgnoreCase(String country);

    List<Circuit> findByActiveTrue();

    List<Circuit> findByActiveFalse();

    boolean existsByNameIgnoreCase(String name);
}