package da_figma.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.enums.BookingStatus;
import da_figma.models.Bike;
import da_figma.models.Booking;
import da_figma.models.BookingBikeAllocation;
import da_figma.models.BookingService;
import da_figma.models.Event;
import da_figma.models.TeamService;
import da_figma.repositories.BikeRepository;
import da_figma.repositories.BookingRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class BookingManagerService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final TeamServiceRepository serviceRepository;
    private final BikeRepository bikeRepository;

    private final BookingPricingService pricingService;
    private final EventService eventService;
    private final UserContextService userContextService;

    public BookingManagerService(
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            TeamServiceRepository serviceRepository,
            BikeRepository bikeRepository,
            BookingPricingService pricingService,
            EventService eventService,
            UserContextService userContextService) {

        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.serviceRepository = serviceRepository;
        this.bikeRepository = bikeRepository;
        this.pricingService = pricingService;
        this.eventService = eventService;
        this.userContextService = userContextService;
    }

    @Transactional
    public Booking createBooking(BookingRequestDto request) {

        String keycloakId = userContextService.getCurrentKeycloakId();

        Event event = eventRepository.findByIdForUpdate(request.getEventId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Evento non trovato"));

        eventService.validateBooking(event);

        BigDecimal totalPrice =
                pricingService.calculatePrice(request);

        Booking booking = new Booking();
        booking.setUserKeycloakId(keycloakId);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPrice);

        booking.setBookingServices(new ArrayList<>());
        booking.setBikeAllocations(new ArrayList<>());

        LocalDate bookingStart = null;
        LocalDate bookingEnd = null;

        // =========================
        // SERVICES PROCESSING
        // =========================
        for (BookingServiceRequestDto serviceRequest : request.getServices()) {

            TeamService service = serviceRepository
                    .findById(serviceRequest.getServiceId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Servizio non trovato"));

            List<LocalDate> dates = new ArrayList<>(serviceRequest.getSelectedDates());
            dates.sort(Comparator.naturalOrder());

            if (dates.isEmpty()) {
                throw new IllegalArgumentException("Seleziona almeno una data per il servizio");
            }

            LocalDate start = dates.get(0);
            LocalDate end = dates.get(dates.size() - 1);

            // update booking range globale
            if (bookingStart == null || start.isBefore(bookingStart)) {
                bookingStart = start;
            }

            if (bookingEnd == null || end.isAfter(bookingEnd)) {
                bookingEnd = end;
            }

            BookingService bs = new BookingService();
            bs.setBooking(booking);
            bs.setService(service);
            bs.setServiceStartDate(start);
            bs.setServiceEndDate(end);
            bs.setDays(dates.size());

            // pricing già calcolato dal pricingService → qui solo reference logica
            bs.setPriceAtBooking(
                    pricingService.calculatePrice(
                            new BookingRequestDto(
                                    request.getEventId(),
                                    request.getBikeId(),
                                    List.of(serviceRequest)
                            )
                    )
            );

            booking.getBookingServices().add(bs);

            // =========================
            // BIKE HANDLING (ONLY BIKE SERVICE)
            // =========================
            if (service.getName().equalsIgnoreCase("Noleggio moto")) {

                if (request.getBikeId() == null) {
                    throw new IllegalArgumentException("Moto obbligatoria per il noleggio");
                }

                Bike bike = bikeRepository.findById(request.getBikeId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Moto non trovata"));

                boolean available = bikeRepository.isAvailable(
                        bike.getId(),
                        start,
                        end
                );

                if (!available) {
                    throw new IllegalArgumentException("Moto non disponibile nel periodo selezionato");
                }

                BookingBikeAllocation allocation = new BookingBikeAllocation();
                allocation.setBooking(booking);
                allocation.setBike(bike);
                allocation.setRentalStartDate(start);
                allocation.setRentalEndDate(end);

                booking.getBikeAllocations().add(allocation);
            }
        }

        booking.setBookingStartDate(bookingStart);
        booking.setBookingEndDate(bookingEnd);

        // =========================
        // CAPACITY MANAGEMENT
        // =========================
        if (event.getMaxParticipants() != null &&
                event.getCurrentParticipants() >= event.getMaxParticipants()) {

            booking.setStatus(BookingStatus.WAITING_LIST);

        } else {

            booking.setStatus(BookingStatus.CONFIRMED);
            eventService.registerBooking(event);
        }

        return bookingRepository.save(booking);
    }
}