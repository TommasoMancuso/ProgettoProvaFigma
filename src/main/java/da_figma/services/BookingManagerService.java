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
import da_figma.enums.PricingType;
import da_figma.models.Bike;
import da_figma.models.Booking;
import da_figma.models.BookingBikeAllocation;
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

        return createBookingInternal(request, keycloakId, false);
   }



   @Transactional
   public Booking createBooking(BookingRequestDto request, String keycloakId) {

    return createBookingInternal(request, keycloakId, false);
   }


   @Transactional
   public Booking createPaidBooking(
                BookingRequestDto request,
                String keycloakId) {

        return createBookingInternal(request, keycloakId, true);
   }



   private Booking createBookingInternal(BookingRequestDto request, String keycloakId, boolean paid) {

        Event event = eventRepository.findByIdForUpdate(request.getEventId()).orElseThrow(() ->
                        new IllegalArgumentException("Evento non trovato"));

        eventService.validateBooking(event);

        BigDecimal totalPrice = pricingService.calculatePrice(request);

        Booking booking = new Booking();

        booking.setUserKeycloakId(keycloakId);
        booking.setEvent(event);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(paid ? "PAID" : "UNPAID");

        LocalDate globalStartDate = null;
        LocalDate globalEndDate = null;

        for (BookingServiceRequestDto serviceRequest : request.getServices()) {

                TeamService service = serviceRepository.findById(serviceRequest.getServiceId())
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Servizio non trovato"));

                List<LocalDate> dates = new ArrayList<>(serviceRequest.getSelectedDates());

                if (dates.isEmpty()) {
                throw new IllegalArgumentException("Date mancanti per il servizio "
                                + service.getName());
                }

                dates.sort(Comparator.naturalOrder());

                LocalDate startDate = dates.get(0);
                LocalDate endDate = dates.get(dates.size() - 1);

                if (globalStartDate == null || startDate.isBefore(globalStartDate)) {

                        globalStartDate = startDate;
                }

                if (globalEndDate == null || endDate.isAfter(globalEndDate)) {

                        globalEndDate = endDate;
                }

                da_figma.models.BookingService bookingService = new da_figma.models.BookingService();

                bookingService.setBooking(booking);
                bookingService.setService(service);

                bookingService.setServiceStartDate(startDate);
                bookingService.setServiceEndDate(endDate);

                bookingService.setDays(dates.size());

                bookingService.setPriceAtBooking(BigDecimal.ZERO);

                booking.getBookingServices().add(bookingService);

                if (service.getPricingType() == PricingType.BIKE_BASED) {

                if (serviceRequest.getBikeId() == null) {

                        throw new IllegalArgumentException(
                                "Moto obbligatoria per il servizio noleggio");
                }

                Bike bike = bikeRepository.findByIdForUpdate(serviceRequest.getBikeId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Moto non trovata"));

                boolean available = bikeRepository.isAvailable(
                                bike.getId(), startDate, endDate);

                if (!available) {

                        throw new IllegalArgumentException(
                                "Moto non disponibile nel periodo selezionato");
                }

                BookingBikeAllocation allocation = new BookingBikeAllocation();

                allocation.setBooking(booking);
                allocation.setBike(bike);

                allocation.setRentalStartDate(startDate);
                allocation.setRentalEndDate(endDate);

                booking.getBikeAllocations().add(allocation);
                }
        }

        booking.setBookingStartDate(globalStartDate);
        booking.setBookingEndDate(globalEndDate);

        if (event.getMaxParticipants() != null && event.getCurrentParticipants()
                >= event.getMaxParticipants()) {

                booking.setStatus(BookingStatus.WAITING_LIST);

        } else {

                booking.setStatus(BookingStatus.CONFIRMED);

                eventService.registerBooking(event);
        }

        return bookingRepository.save(booking);
    }



}