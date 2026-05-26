package da_figma.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import da_figma.enums.PricingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "services")
public class TeamService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PricingType pricingType;

    // SOLO PER FIXED
    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    // se in futuro vuoi MANUAL override dal manager
    @Column(name = "manual_price", precision = 10, scale = 2)
    private BigDecimal manualPrice;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "service")
    private List<BookingService> bookingServices;
}