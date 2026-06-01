package da_figma.services;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.BookingRequestDto;
import da_figma.enums.BookingStatus;
import da_figma.models.Bike;
import da_figma.models.Booking;
import da_figma.models.BookingBikeAllocation;
import da_figma.models.BookingService;
import da_figma.models.Event;
import da_figma.models.EventServicePrice;
import da_figma.models.TeamService;
import da_figma.repositories.BikeRepository;
import da_figma.repositories.BookingBikeAllocationRepository;
import da_figma.repositories.BookingRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.EventServicePriceRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class BookingManagerService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final BikeRepository bikeRepository;
    private final TeamServiceRepository serviceRepository;
    private final EventServicePriceRepository eventServicePriceRepository;
    private final BookingBikeAllocationRepository bookingBikeAllocationRepository;
    private final EventService eventService;
    private final UserContextService userContextService;

    public BookingManagerService(
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            BikeRepository bikeRepository,
            TeamServiceRepository serviceRepository,
            EventServicePriceRepository eventServicePriceRepository,
            BookingBikeAllocationRepository bookingBikeAllocationRepository,
            EventService eventService,
            UserContextService userContextService
    ) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.bikeRepository = bikeRepository;
        this.serviceRepository = serviceRepository;
        this.eventServicePriceRepository = eventServicePriceRepository;
        this.bookingBikeAllocationRepository = bookingBikeAllocationRepository;
        this.eventService = eventService;
        this.userContextService = userContextService;
    }

    // =========================
    // CREATE BOOKING (KEYCLOAK USER)
    // =========================
    @Transactional
    public Booking createBooking(BookingRequestDto request) {

        // =========================
        // 1. USER LOGGATO (KEYCLOAK)
        // =========================
        String keycloakId = userContextService.getCurrentKeycloakId();

        // =========================
        // 2. EVENT
        // =========================
        Event event = eventRepository.findByIdForUpdate(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event non trovato"));

        eventService.validateBooking(event);

        // =========================
        // 3. BIKE (OPTIONAL)
        // =========================
        Bike bike = null;

        if (request.getBikeId() != null) {

            boolean available = bikeRepository.isAvailable(
                    request.getBikeId(),
                    event.getStartDate(),
                    event.getEndDate()
            );

            if (available) {
                bike = bikeRepository.findById(request.getBikeId())
                        .orElse(null);
            }
        }

        // =========================
        // 4. BOOKING BASE
        // =========================
        Booking booking = new Booking();

        booking.setUserKeycloakId(keycloakId);

        booking.setEvent(event);
        booking.setBike(bike);
        booking.setBookingServices(new ArrayList<>());
        booking.setStatus(BookingStatus.PENDING);

        // =========================
        // 5. SERVICES
        // =========================
        BigDecimal total = BigDecimal.ZERO;

        if (request.getServiceIds() != null) {

            for (Long serviceId : request.getServiceIds()) {

                TeamService service = serviceRepository.findById(serviceId)
                        .orElse(null);

                if (service == null) continue;

                EventServicePrice esp = eventServicePriceRepository
                        .findByEventIdAndServiceId(event.getId(), serviceId)
                        .orElse(null);

                BookingService bookingService = new BookingService();
                bookingService.setBooking(booking);
                bookingService.setService(service);

                if (esp != null && esp.getPriceOneDay() != null) {
                    bookingService.setPriceAtBooking(esp.getPriceOneDay());
                    total = total.add(esp.getPriceOneDay());
                }

                booking.getBookingServices().add(bookingService);
            }
        }

        // =========================
        // 6. CAPACITY CHECK
        // =========================
        if (event.getMaxParticipants() != null &&
            event.getCurrentParticipants() >= event.getMaxParticipants()) {

            booking.setStatus(BookingStatus.WAITING_LIST);

        } else {

            booking.setStatus(BookingStatus.CONFIRMED);
            eventService.registerBooking(event);
        }

        // =========================
        // 7. BIKE ALLOCATION
        // =========================
        if (bike != null && booking.getStatus() == BookingStatus.CONFIRMED) {

            BookingBikeAllocation allocation = new BookingBikeAllocation();
            allocation.setBike(bike);
            allocation.setBooking(booking);
            allocation.setRentalStartDate(event.getStartDate());
            allocation.setRentalEndDate(event.getEndDate());

            bookingBikeAllocationRepository.save(allocation);
        }

        // =========================
        // 8. TOTAL PRICE
        // =========================
        booking.setTotalPrice(total);

        // =========================
        // 9. SAVE
        // =========================
        return bookingRepository.save(booking);
    }
}