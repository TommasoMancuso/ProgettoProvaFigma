package da_figma.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

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

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // USER
    // =========================

    @Column(name = "user_keycloak_id", nullable = false)
    private String userKeycloakId;

    // =========================
    // BOOKING JSON
    // =========================

    @Lob
    @Column(name = "booking_json", nullable = false)
    private String bookingJson;

    // =========================
    // PRICES
    // =========================

    /**
     * Prezzo della nuova configurazione
     * contenuta nel bookingJson.
     *
     * Per una nuova prenotazione:
     * è semplicemente il prezzo della prenotazione.
     *
     * Per una modifica:
     * è il nuovo prezzo della prenotazione.
     */
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Prezzo della prenotazione prima della modifica.
     *
     * NULL per una nuova prenotazione.
     */
    @Column(name = "original_total_price", precision = 10, scale = 2)
    private BigDecimal originalTotalPrice;

    /**
     * Differenza tra nuovo e vecchio prezzo.
     *
     * Esempi:
     *
     * +50 -> l'utente deve pagare 50 euro
     *   0 -> nessuna differenza
     * -50 -> l'utente matura 50 euro di credito
     *
     * Per una nuova prenotazione può essere NULL.
     */
    @Column(name = "difference_amount", precision = 10, scale = 2)
    private BigDecimal differenceAmount;

    // =========================
    // EXISTING BOOKING
    // =========================

    /**
     * ID della prenotazione esistente che si sta modificando.
     *
     * NULL quando la PendingBooking rappresenta
     * una nuova prenotazione.
     */
    @Column(name = "booking_id")
    private Long bookingId;

    // =========================
    // OPERATION
    // =========================

    /**
     * Tipo di operazione.
     *
     * Esempi:
     * CREATE
     * UPDATE
     */
    @Column(name = "operation_type", length = 30)
    private String operationType;

    // =========================
    // STRIPE
    // =========================

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "payment_status", length = 30)
    private String paymentStatus;

    // =========================
    // TIMESTAMP
    // =========================

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // =========================
    // CREATION
    // =========================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }
    }
}
