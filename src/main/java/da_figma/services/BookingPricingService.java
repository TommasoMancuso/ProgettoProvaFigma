package da_figma.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.enums.BookingDuration;
import da_figma.models.Bike;
import da_figma.models.Event;
import da_figma.models.EventServicePrice;
import da_figma.models.TeamService;
import da_figma.repositories.BikeRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.EventServicePriceRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class BookingPricingService {

    private final EventRepository eventRepository;
    private final EventServicePriceRepository eventServicePriceRepository;
    private final TeamServiceRepository serviceRepository;
    private final BikeRepository bikeRepository;
    private final BookingDurationResolver durationResolver;

    public BookingPricingService(
            EventRepository eventRepository,
            EventServicePriceRepository eventServicePriceRepository,
            TeamServiceRepository serviceRepository,
            BikeRepository bikeRepository,
            BookingDurationResolver durationResolver) {

        this.eventRepository = eventRepository;
        this.eventServicePriceRepository = eventServicePriceRepository;
        this.serviceRepository = serviceRepository;
        this.bikeRepository = bikeRepository;
        this.durationResolver = durationResolver;
    }

    public BigDecimal calculatePrice(BookingRequestDto request) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Evento non trovato"));

        BigDecimal total = BigDecimal.ZERO;

        if (request.getServices() == null || request.getServices().isEmpty()) {
            return total;
        }

        for (BookingServiceRequestDto serviceRequest : request.getServices()) {

            TeamService service = serviceRepository
                    .findById(serviceRequest.getServiceId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Servizio non trovato"));

            BookingDuration duration =
                    durationResolver.resolve(
                            event,
                            serviceRequest.getSelectedDates());

            BigDecimal servicePrice;

            switch (service.getPricingType()) {

                case FIXED -> {

                    if (service.getBasePrice() == null) {
                        throw new IllegalArgumentException(
                                "Prezzo fisso non configurato per il servizio " + service.getName());
                    }

                    servicePrice = service.getBasePrice();
                }

                case EVENT_BASED -> {

                    EventServicePrice priceConfig =
                            eventServicePriceRepository
                                    .findByEventIdAndServiceId(
                                            event.getId(),
                                            service.getId())
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    "Prezzo evento non configurato"));

                    servicePrice = getPriceByDuration(priceConfig, duration);
                }

                case BIKE_BASED -> {

            if (serviceRequest.getBikeId() == null) {
                throw new IllegalArgumentException(
                        "È necessario selezionare una moto");
            }

            Bike bike = bikeRepository
                    .findById(serviceRequest.getBikeId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Moto non trovata"));

            if (bike.getDailyPrice() == null) {
                throw new IllegalArgumentException(
                        "Prezzo giornaliero moto non configurato");
            }

            servicePrice = bike.getDailyPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    serviceRequest.getSelectedDates().size()
                            )
                    );
        }

                case MANUAL -> throw new IllegalArgumentException(
                        "Servizio disponibile solo tramite contatto diretto");

                default -> throw new IllegalArgumentException(
                        "Tipo di pricing non supportato");
            }

            total = total.add(servicePrice);
        }

        return total;
    }

    private BigDecimal getPriceByDuration(
            EventServicePrice priceConfig,
            BookingDuration duration) {

        return switch (duration) {

            case ONE_DAY -> {
                if (priceConfig.getPriceOneDay() == null) {
                    throw new IllegalArgumentException("Prezzo 1 giorno non configurato");
                }
                yield priceConfig.getPriceOneDay();
            }

            case TWO_DAYS -> {
                if (priceConfig.getPriceTwoDays() == null) {
                    throw new IllegalArgumentException("Prezzo 2 giorni non configurato");
                }
                yield priceConfig.getPriceTwoDays();
            }

            case THREE_DAYS -> {
                if (priceConfig.getPriceThreeDays() == null) {
                    throw new IllegalArgumentException("Prezzo 3 giorni non configurato");
                }
                yield priceConfig.getPriceThreeDays();
            }

            case FULL_EVENT -> {
                if (priceConfig.getPriceFullEvent() == null) {
                    throw new IllegalArgumentException("Prezzo full event non configurato");
                }
                yield priceConfig.getPriceFullEvent();
            }
        };
    }
}