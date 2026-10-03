package da_figma.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import da_figma.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDetailDto {

    private BookingInfo booking = new BookingInfo();

    private UserInfo user = new UserInfo();

    private List<ServiceInfo> services = new ArrayList<>();

    private BikeInfo bike;

    // =====================================================
    // BOOKING
    // =====================================================

    @Getter
    @Setter
    public static class BookingInfo {

        private Long bookingId;

        private Long eventId;

        private String eventName;

        private String organizer;

        private String circuitName;

        private LocalDate eventStartDate;

        private LocalDate eventEndDate;

        private LocalDate bookingStartDate;

        private LocalDate bookingEndDate;

        private BookingStatus bookingStatus;

        private String paymentStatus;

        private BigDecimal totalPrice;
    }

    // =====================================================
    // USER
    // =====================================================

    @Getter
    @Setter
    public static class UserInfo {

        private String username;

        private String firstName;

        private String lastName;

        private String email;

        private String phone;

        private LocalDate birthDate;
    }

    // =====================================================
    // SERVICES
    // =====================================================

    @Getter
    @Setter
    public static class ServiceInfo {

        private String serviceName;

        private BigDecimal price;

        
    }

    // =====================================================
    // BIKE
    // =====================================================

    @Getter
    @Setter
    public static class BikeInfo {

        private String brand;

        private String model;

        private String chassisNumber;
    }

}