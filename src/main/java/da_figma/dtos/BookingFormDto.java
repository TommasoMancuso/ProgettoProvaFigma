package da_figma.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingFormDto {

    private Long eventId;

    private String eventName;

    private String organizer;

    private String circuitName;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<LocalDate> eventDates;

    private List<ServiceBookingDto> services;

}