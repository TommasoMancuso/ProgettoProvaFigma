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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @Size(max = 50)
    @NotBlank
    private String name;

    @Column(nullable = false, length = 50)
    @Size(max = 50)
    @NotBlank
    private String surname;

    @Column(nullable = false, unique = true, length = 100)
    @Size(max = 100)
    @NotBlank
    private String email;

    @Column(nullable = false, length = 255)
    @Size(max = 255)
    @NotBlank
    private String password;

    @Column(nullable = false, length = 20)
    @Size(max = 20)
    @NotBlank
    private String phone;

    @Column(nullable = false)
    @NotBlank
    private LocalDate birthDate;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = true)
    private String instagram;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // =========================
    // CREATED AT AUTOMATICO
    // =========================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

}
