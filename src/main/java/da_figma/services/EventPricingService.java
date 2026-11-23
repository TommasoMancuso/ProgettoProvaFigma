package da_figma.services;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import da_figma.dtos.EventPricingDto;
import da_figma.dtos.EventPricingRowDto;
import da_figma.models.Event;
import da_figma.models.EventServicePrice;
import da_figma.repositories.EventRepository;
import da_figma.repositories.EventServicePriceRepository;

@Service
public class EventPricingService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventServicePriceRepository priceRepository;

    public EventPricingDto getPricingForm(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Evento non trovato"));

        List<EventServicePrice> prices =
                priceRepository.findByEventId(eventId);

        List<EventPricingRowDto> rows = new ArrayList<>();

        for (EventServicePrice price : prices) {

            /*
             * NON mostrare il servizio noleggio moto.
             * Il suo prezzo verrà determinato dalla moto scelta.
             */

            if ("BIKE_BASED".equals(
                    price.getService()
                            .getPricingType()
                            .name())) {

                continue;
            }

            EventPricingRowDto row =
                    new EventPricingRowDto();

            row.setServiceId(
                    price.getService().getId());

            row.setServiceName(
                    price.getService().getName());

            row.setPriceOneDay(
                    price.getPriceOneDay());

            row.setPriceTwoDays(
                    price.getPriceTwoDays());

            row.setPriceThreeDays(
                    price.getPriceThreeDays());

            row.setPriceFullEvent(
                    price.getPriceFullEvent());

            rows.add(row);
        }

        EventPricingDto dto =
                new EventPricingDto();

        dto.setEventId(event.getId());
        dto.setRows(rows);

        return dto;
    }

    public void savePrices(EventPricingDto dto) {

        for (EventPricingRowDto row : dto.getRows()) {

            EventServicePrice price =
                    priceRepository
                            .findByEventIdAndServiceId(
                                    dto.getEventId(),
                                    row.getServiceId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Configurazione non trovata"));

            price.setPriceOneDay(
                    row.getPriceOneDay());

            price.setPriceTwoDays(
                    row.getPriceTwoDays());

            price.setPriceThreeDays(
                    row.getPriceThreeDays());

            price.setPriceFullEvent(
                    row.getPriceFullEvent());

            priceRepository.save(price);
        }
    }

    public long getEventDurationDays(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Evento non trovato"));

        return ChronoUnit.DAYS.between(
                        event.getStartDate(),
                        event.getEndDate())
                + 1;
    }
}