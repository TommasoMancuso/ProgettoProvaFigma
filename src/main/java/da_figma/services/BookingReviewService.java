package da_figma.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingReviewDto;
import da_figma.dtos.BookingReviewServiceDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.models.Bike;
import da_figma.models.Event;
import da_figma.models.TeamService;
import da_figma.repositories.BikeRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class BookingReviewService {

    private final EventRepository eventRepository;
    private final TeamServiceRepository serviceRepository;
    private final BikeRepository bikeRepository;
    private final BookingPricingService bookingPricingService;

    public BookingReviewService(
            EventRepository eventRepository,
            TeamServiceRepository serviceRepository,
            BikeRepository bikeRepository,
            BookingPricingService bookingPricingService) {

        this.eventRepository = eventRepository;
        this.serviceRepository = serviceRepository;
        this.bikeRepository = bikeRepository;
        this.bookingPricingService = bookingPricingService;
    }

    public BookingReviewDto buildReview(BookingRequestDto request) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Evento non trovato"));

        BookingReviewDto dto = new BookingReviewDto();

        dto.setEventId(event.getId());
        dto.setEventName(event.getName());
        dto.setCircuitName(event.getCircuit().getName());

        List<BookingReviewServiceDto> services = new ArrayList<>();

        String selectedBike = null;

        for (BookingServiceRequestDto serviceRequest : request.getServices()) {

            TeamService service = serviceRepository.findById(serviceRequest.getServiceId())
                    .orElseThrow(() ->
                            new IllegalArgumentException( "Servizio non trovato"));

            BookingReviewServiceDto row = new BookingReviewServiceDto();

            row.setServiceName(service.getName());

            row.setDates(serviceRequest.getSelectedDates());

            row.setPrice(bookingPricingService.calculateSingleServicePrice(event, serviceRequest));

            services.add(row);

            if (serviceRequest.getBikeId() != null) {

                Bike bike = bikeRepository.findById(serviceRequest.getBikeId())
                                            .orElse(null);

                if (bike != null) {

                    selectedBike = bike.getBrand() + " " + bike.getModel();
                }
            }
        }

        dto.setBikeName(selectedBike);

        dto.setServices(services);

        BigDecimal total = bookingPricingService.calculatePrice(request);

        dto.setTotalPrice(total);

        return dto;
    }
}