package da_figma.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventPricingRowDto {

    private Long serviceId;

    private String serviceName;

    private BigDecimal priceOneDay;

    private BigDecimal priceTwoDays;

    private BigDecimal priceThreeDays;

    private BigDecimal priceFullEvent;
}
