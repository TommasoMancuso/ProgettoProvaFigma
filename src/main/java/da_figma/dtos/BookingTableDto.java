package da_figma.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import da_figma.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingTableDto {

    private Long bookingId;

    private UserBookingDto user;

    private String eventName;

    private String organizer;

    private String circuitName;

    private LocalDate bookingStartDate;

    private LocalDate bookingEndDate;

    private BigDecimal totalPrice;

    private BookingStatus status;
}