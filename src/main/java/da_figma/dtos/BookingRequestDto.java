package da_figma.dtos;

import java.util.List;

public class BookingRequestDto {

    private Long userId;
    private Long eventId;

    private List<Long> serviceIds;

    private Long bikeId; // opzionale

    // getters and setters
}
