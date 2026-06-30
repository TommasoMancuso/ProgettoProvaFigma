package da_figma.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import da_figma.models.PendingBooking;

public interface PendingBookingRepository extends JpaRepository<PendingBooking, Long> {

    Optional<PendingBooking> findByStripeSessionId(String stripeSessionId);
}