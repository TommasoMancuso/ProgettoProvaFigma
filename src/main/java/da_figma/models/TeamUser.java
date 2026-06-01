package da_figma.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "team_users")
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String keycloakId;

    // =========================
    // ANAGRAFICA
    // =========================
    @Column(nullable = false, length = 50)
    @Size(max = 50)
    @NotBlank
    private String name;

    @Column(nullable = false, length = 50)
    @Size(max = 50)
    @NotBlank
    private String surname;

    @Column(nullable = false, length = 20)
    @Size(max = 20)
    @NotBlank
    private String phone;

    @Column(name = "birth_date", nullable = false)
    @NotNull
    private LocalDate birthDate;

    // =========================
    // PROFILO
    // =========================
    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profileImage;

    private String instagram;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_role_id", nullable = false)
    private TeamRole teamRole;

    // =========================
    // CREAZIONE AUTOMATICA
    // =========================
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}