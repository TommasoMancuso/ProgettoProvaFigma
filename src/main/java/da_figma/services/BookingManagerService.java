package da_figma.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.BookingDetailDto;
import da_figma.dtos.BookingTableDto;
import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.dtos.UserBookingDto;
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
        
        @Autowired
        private KeycloakUserService keycloakUserService;
        
        @Autowired
        private BookingRepository bookingRepository;
        
        @Autowired
        private EventRepository eventRepository;
        
        @Autowired
        private TeamServiceRepository serviceRepository;
        
        @Autowired
        private BikeRepository bikeRepository;
        
        @Autowired
        private BookingPricingService pricingService;
        
        @Autowired
        private EventService eventService;
        
        @Autowired
        private UserContextService userContextService;
        
        
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
        public Booking createPaidBooking(BookingRequestDto request, String keycloakId) {
                
                return createBookingInternal(request, keycloakId, true);
        }
        
        @Transactional(readOnly = true)
        public List<BookingTableDto> getMyBookings() {
                
                String keycloakId = userContextService.getCurrentKeycloakId();
                
                List<Booking> bookings =
                bookingRepository.findByUserKeycloakId(keycloakId);
                
                List<BookingTableDto> result = new ArrayList<>();
                
                for (Booking booking : bookings) {
                        
                        BookingTableDto dto = new BookingTableDto();
                        
                        // =========================
                        // PRENOTAZIONE
                        // =========================
                        
                        dto.setBookingId(booking.getId());
                        
                        dto.setBookingStartDate(booking.getBookingStartDate());
                        
                        dto.setBookingEndDate(booking.getBookingEndDate());
                        
                        dto.setTotalPrice(booking.getTotalPrice());
                        
                        dto.setStatus(booking.getStatus());
                        
                        // =========================
                        // EVENTO
                        // =========================
                        
                        Event event = booking.getEvent();
                        
                        dto.setEventName(event.getName());
                        
                        dto.setOrganizer(event.getOrganizer());
                        
                        dto.setCircuitName(event.getCircuit().getName());
                        
                        result.add(dto);
                }
                
                return result;
        }

        @Transactional(readOnly = true)
        public BookingRequestDto buildBookingRequestForEdit(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException("Prenotazione non trovata"));

                BookingRequestDto request = new BookingRequestDto();

                // =========================
                // EVENTO
                // =========================

                request.setEventId(
                        booking.getEvent().getId()
                );

                // =========================
                // PAGAMENTO
                // =========================

                request.setPaymentMethod(
                        booking.getPaymentStatus()
                );

                // =========================
                // SERVIZI
                // =========================

                for (da_figma.models.BookingService bookingService :
                        booking.getBookingServices()) {

                        BookingServiceRequestDto serviceRequest =
                                new BookingServiceRequestDto();

                        // ID SERVIZIO
                        serviceRequest.setServiceId(
                                bookingService.getService().getId()
                        );

                        // =========================
                        // DATE SELEZIONATE
                        // =========================

                        LocalDate startDate =
                                bookingService.getServiceStartDate();

                        LocalDate endDate =
                                bookingService.getServiceEndDate();

                        List<LocalDate> selectedDates =
                                new ArrayList<>();

                        LocalDate current = startDate;

                        while (!current.isAfter(endDate)) {

                        selectedDates.add(current);

                        current = current.plusDays(1);
                        }

                        serviceRequest.setSelectedDates(selectedDates);

                        // =========================
                        // MOTO
                        // =========================

                        if (bookingService.getService().getPricingType()
                                == PricingType.BIKE_BASED
                                && !booking.getBikeAllocations().isEmpty()) {

                        Long bikeId =
                                booking.getBikeAllocations()
                                        .getFirst()
                                        .getBike()
                                        .getId();

                        serviceRequest.setBikeId(bikeId);
                        }

                        request.getServices().add(serviceRequest);
                }

                return request;
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
                                throw new IllegalArgumentException("Date mancanti per il servizio " + service.getName());
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
                        
                        // ===========================================
                        // CALCOLO PREZZO DEL SINGOLO SERVIZIO
                        // ===========================================
                        
                        BigDecimal singleServicePrice = pricingService.calculateSingleServicePrice(event, serviceRequest);
                        
                        bookingService.setPriceAtBooking(singleServicePrice);
                        
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
        
        @Transactional(readOnly = true)
        public BookingDetailDto getBookingDetail(Long bookingId) {
                
                Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
                
                UserBookingDto user = keycloakUserService.getUserById(booking.getUserKeycloakId());
                
                BookingDetailDto dto = new BookingDetailDto();
                
                // =====================================================
                // BOOKING
                // =====================================================
                
                BookingDetailDto.BookingInfo bookingInfo = dto.getBooking();
                
                bookingInfo.setBookingId(booking.getId());
                
                bookingInfo.setBookingStatus(booking.getStatus());
                
                bookingInfo.setPaymentStatus(booking.getPaymentStatus());
                
                bookingInfo.setTotalPrice(booking.getTotalPrice());
                
                bookingInfo.setBookingStartDate(booking.getBookingStartDate());
                
                bookingInfo.setBookingEndDate(booking.getBookingEndDate());
                
                // =====================================================
                // EVENTO
                // =====================================================
                
                Event event = booking.getEvent();
                
                bookingInfo.setEventId(event.getId());
                
                bookingInfo.setEventName(event.getName());
                
                bookingInfo.setOrganizer(event.getOrganizer());
                
                bookingInfo.setCircuitName(event.getCircuit().getName());
                
                bookingInfo.setEventStartDate(event.getStartDate());
                
                bookingInfo.setEventEndDate(event.getEndDate());
                
                // =====================================================
                // UTENTE
                // =====================================================
                
                BookingDetailDto.UserInfo userInfo = dto.getUser();
                
                userInfo.setUsername(user.getUsername());
                
                userInfo.setFirstName(user.getName());
                
                userInfo.setLastName(user.getSurname());
                
                userInfo.setEmail(user.getEmail());
                
                userInfo.setPhone(user.getPhone());
                
                userInfo.setBirthDate(user.getBirthDate());
                
                // =====================================================
                // SERVIZI
                // =====================================================
                
                for (da_figma.models.BookingService bookingService : booking.getBookingServices()) {
                        
                        BookingDetailDto.ServiceInfo serviceInfo = new BookingDetailDto.ServiceInfo();
                        
                        serviceInfo.setServiceName(bookingService.getService().getName());
                        
                        serviceInfo.setPrice(bookingService.getPriceAtBooking());
                        
                        dto.getServices().add(serviceInfo);
                }
                
                // =====================================================
                // MOTO
                // =====================================================
                
                if (!booking.getBikeAllocations().isEmpty()) {
                        
                        Bike bike = booking.getBikeAllocations().getFirst().getBike();
                        
                        BookingDetailDto.BikeInfo bikeInfo = new BookingDetailDto.BikeInfo();
                        
                        bikeInfo.setBrand(bike.getBrand());
                        
                        bikeInfo.setModel(bike.getModel());
                        
                        bikeInfo.setChassisNumber(bike.getChassisNumber());
                        
                        dto.setBike(bikeInfo);
                }
                
                return dto;
        }
        
}