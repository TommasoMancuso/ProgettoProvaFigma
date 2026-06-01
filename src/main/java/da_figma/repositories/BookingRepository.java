package da_figma.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import da_figma.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_KeycloakId(String keycloakId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByStatus(String status);

    List<Booking> findByPaymentStatus(String paymentStatus);
}