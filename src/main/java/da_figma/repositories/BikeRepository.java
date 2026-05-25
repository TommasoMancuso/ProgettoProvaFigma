package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.Bike;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {

    List<Bike> findByBrandContainingIgnoreCase(String brand);

    List<Bike> findByCategory(String category);

    List<Bike> findByModelContainingIgnoreCase(String model);
}
