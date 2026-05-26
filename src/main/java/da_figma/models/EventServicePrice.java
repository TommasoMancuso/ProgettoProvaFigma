package da_figma.models;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "event_service_prices",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "service_id"})
    }
)
public class EventServicePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // PRICING (DECISO DAL MANAGER)
    // =========================

    @Column(name = "price_one_day", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceOneDay;

    @Column(name = "price_two_days", precision = 10, scale = 2)
    private BigDecimal priceTwoDays;

    @Column(name = "price_full_event", precision = 10, scale = 2)
    private BigDecimal priceFullEvent;

     // =========================
    // RELAZIONI
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}