package da_figma.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import da_figma.models.EventServicePrice;

@Repository
public interface EventServicePriceRepository extends JpaRepository<EventServicePrice, Long> {

    // =========================
    // FIND BY EVENT + SERVICE
    // =========================
    Optional<EventServicePrice> findByEventIdAndServiceId(Long eventId, Long serviceId);

    // =========================
    // ALL PRICES FOR EVENT
    // =========================
    List<EventServicePrice> findByEventId(Long eventId);

    // =========================
    // ALL EVENTS FOR SERVICE
    // =========================
    List<EventServicePrice> findByServiceId(Long serviceId);

    // =========================
    // CHECK EXISTENCE (OPTIONAL SAFETY)
    // =========================
    boolean existsByEventIdAndServiceId(Long eventId, Long serviceId);

    // =========================
    // OPTIONAL: CUSTOM QUERY (if needed later for analytics)
    // =========================
    @Query("""
        SELECT esp
        FROM EventServicePrice esp
        WHERE esp.event.id = :eventId
          AND esp.service.id = :serviceId
    """)
    Optional<EventServicePrice> findPrice(
            @Param("eventId") Long eventId,
            @Param("serviceId") Long serviceId
    );
}