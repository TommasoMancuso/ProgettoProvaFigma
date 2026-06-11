package da_figma.dtos;

import java.math.BigDecimal;

import da_figma.enums.BikeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BikeDto {

    @NotNull
    @NotBlank
    private String brand;

    @NotNull
    @NotBlank
    private String model;

    @NotNull
    @NotBlank
    private Integer year;

    @NotNull
    @NotBlank
    private BikeCategory category;

    @NotNull
    @NotBlank
    private String chassisNumber;

    @NotNull
    @NotBlank
    private Long currentKm;

    @NotNull
    @NotBlank
    private BigDecimal dailyPrice;
}