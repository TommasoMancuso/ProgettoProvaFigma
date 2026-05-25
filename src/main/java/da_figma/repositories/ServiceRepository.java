package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByNameContainingIgnoreCase(String name);

    List<Service> findByActiveTrue();
}
