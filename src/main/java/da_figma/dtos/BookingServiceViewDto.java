package da_figma.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import da_figma.enums.PricingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingServiceViewDto {

    private Long serviceId;

    private String serviceName;

    private PricingType pricingType;

    private List<LocalDate> availableDates;

    private List<BikeOptionDto> availableBikes;

    private BigDecimal previewPrice;

}