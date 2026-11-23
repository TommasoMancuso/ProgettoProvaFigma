package da_figma.dtos;

import java.math.BigDecimal;
import java.util.List;

import da_figma.enums.PricingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceBookingDto {

    private Long serviceId;

    private String serviceName;

    private PricingType pricingType;

    private BigDecimal priceOneDay;

    private BigDecimal priceTwoDays;

    private BigDecimal priceThreeDays;

    private BigDecimal priceFullEvent;

    private List<BikeOptionDto> availableBikes;

}