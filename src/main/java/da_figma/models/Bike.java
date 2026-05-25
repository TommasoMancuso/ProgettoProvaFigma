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
@Table(name = "bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String model;

    @Column(nullable = false)
    @NotNull
    private Integer year;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String category;

    // Relazioni

    @OneToMany(mappedBy = "bike")
    private List<BookingBikeAllocation> bikeAllocations;
}
