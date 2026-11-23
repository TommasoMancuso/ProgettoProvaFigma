package da_figma.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingServiceRequestDto {

    private Long serviceId;

    private Long bikeId;

    private List<LocalDate> selectedDates = new ArrayList<>();
}