package da_figma.dtos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import da_figma.enums.EventStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {

    // =========================
    // BASIC INFO
    // =========================

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;

    @Size(max = 150)
    private String organizer;

    // =========================
    // RELATION
    // =========================

    @NotNull
    private Long circuitId;

    // =========================
    // DATE RANGE
    // =========================

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    // =========================
    // CAPACITY
    // =========================

    @NotNull
    @Min(1)
    private Integer maxParticipants;

    // =========================
    // STATUS (OPTIONAL FOR MVP)
    // =========================

    private EventStatus status;

    private List<Long> serviceIds;
}