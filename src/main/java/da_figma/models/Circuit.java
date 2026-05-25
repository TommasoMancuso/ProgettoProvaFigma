package da_figma.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "circuits")
public class Circuit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column(length = 100)
    @Size(max = 100)
    private String country;

    @Column(name = "length_km", precision = 5, scale = 2)
    private Double lengthKm;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Relazioni

    @OneToMany(mappedBy = "circuit")
    private List<Event> events;
}