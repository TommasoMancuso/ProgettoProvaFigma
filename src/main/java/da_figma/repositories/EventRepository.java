package da_figma.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import da_figma.enums.EventStatus;
import da_figma.models.Event;
import jakarta.persistence.LockModeType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // =========================
    // SEARCH
    // =========================

    List<Event> findByStartDateAfter(LocalDate date);

    List<Event> findByEndDateBefore(LocalDate date);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByStatusNot(EventStatus status);

    List<Event> findByNameContainingIgnoreCase(String name);

    List<Event> findByOrganizerContainingIgnoreCase(String organizer);

    List<Event> findByCircuit_NameContainingIgnoreCase(String circuitName);

    // =========================
    // PESSIMISTIC LOCK (BOOKING SAFE)
    // =========================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);

    // =========================
    // BUSINESS QUERY
    // =========================

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.event.id = :eventId
        AND b.status = 'CONFIRMED'
    """)
    Long countConfirmedBookings(@Param("eventId") Long eventId);
}