package da_figma.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private Long userId;

    private Long eventId;

    private LocalDate bikeStartDate;

    private LocalDate bikeEndDate;

    private List<Long> serviceIds;
    
    private Long bikeId;
}