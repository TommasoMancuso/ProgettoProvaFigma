package da_figma.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pending_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_keycloak_id", nullable = false)
    private String userKeycloakId;

    @Lob
    @Column(name = "booking_json", nullable = false)
    private String bookingJson;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "payment_status", length = 30)
    private String paymentStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}