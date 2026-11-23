package da_figma.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BikeOptionDto {

    private Long id;

    private String name;

    private BigDecimal dailyPrice;

}