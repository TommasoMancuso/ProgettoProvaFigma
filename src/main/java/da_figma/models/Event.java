package da_figma.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull
    private LocalDate endDate;

    @Column(length = 150)
    @Size(max = 150)
    private String organizer;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String status = "AVAILABLE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Relazioni
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_id", nullable = false)
    private Circuit circuit;


    @OneToMany(mappedBy = "event")
    private List<Booking> bookings;
}