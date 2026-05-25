package da_figma.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartDateAfter(LocalDate date);

    List<Event> findByEndDateBefore(LocalDate date);

    List<Event> findByStatus(String status);

    List<Event> findByNameContainingIgnoreCase(String name);

    List<Event> findByOrganizerContainingIgnoreCase(String organizer);

    List<Event> findByCircuit_NameContainingIgnoreCase(String circuitName);

}