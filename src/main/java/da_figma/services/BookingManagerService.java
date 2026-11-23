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
import da_figma.dtos.BookingRequestDto;
import da_figma.dtos.BookingServiceRequestDto;
import da_figma.dtos.BookingTableDto;
import da_figma.dtos.UserBookingDto;
import da_figma.enums.BookingPriceDifferenceType;
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
                
                List<Booking> bookings = bookingRepository.findByUserKeycloakId(keycloakId);
                
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

                        dto.setCreditAmount(booking.getCreditAmount());
                        
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

                bookingInfo.setCreditAmount(booking.getCreditAmount());
                
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

        @Transactional
        public Booking updateBooking(Long bookingId, BookingRequestDto request) {

                String keycloakId = userContextService.getCurrentKeycloakId();

                return updateBookingInternal( bookingId, request, keycloakId, BigDecimal.ZERO, false);
        }


        @Transactional
        public Booking applyPendingModification(
                Long bookingId,
                BookingRequestDto request,
                String keycloakId,
                BigDecimal differenceAmount) {

                return updateBookingInternal(bookingId, request, keycloakId, differenceAmount, true);
        }



        private Booking updateBookingInternal(Long bookingId, BookingRequestDto request,
                                                String keycloakId, BigDecimal differenceAmount,
                                                boolean paymentConfirmed) {

                // =====================================================
                // PRENOTAZIONE
                // =====================================================

                Booking booking =
                        bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Prenotazione non trovata"));

                // =====================================================
                // SICUREZZA
                // =====================================================

                if (!booking.getUserKeycloakId().equals(keycloakId)) {

                        throw new IllegalArgumentException(
                                "Non puoi modificare questa prenotazione");
                }

                // =====================================================
                // VALIDAZIONE REQUEST
                // =====================================================

                if (request == null) {

                        throw new IllegalArgumentException(
                                "Dati della prenotazione mancanti");
                }

                if (request.getEventId() == null) {

                        throw new IllegalArgumentException(
                                "Evento non specificato");
                }

                if (request.getServices() == null ||
                        request.getServices().isEmpty()) {

                        throw new IllegalArgumentException(
                                "Deve essere presente almeno un servizio");
                }

                // =====================================================
                // EVENTO
                // =====================================================

                Event event =
                        eventRepository.findByIdForUpdate(
                                request.getEventId()
                        ).orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento non trovato"));

                eventService.validateBooking(event);

                // =====================================================
                // NON PERMETTIAMO CAMBIO EVENTO
                // =====================================================

                if (!booking.getEvent().getId()
                        .equals(event.getId())) {

                        throw new IllegalArgumentException(
                                "Non è possibile cambiare evento durante la modifica");
                }

                // =====================================================
                // CALCOLO NUOVO PREZZO
                // =====================================================

                BigDecimal totalPrice =
                        pricingService.calculatePrice(request);

                // =====================================================
                // NUOVI SERVIZI
                // =====================================================

                List<da_figma.models.BookingService>
                        newBookingServices =
                        new ArrayList<>();

                // =====================================================
                // NUOVE MOTO
                // =====================================================

                List<BookingBikeAllocation>
                        newBikeAllocations =
                        new ArrayList<>();

                // =====================================================
                // DATE GLOBALI
                // =====================================================

                LocalDate globalStartDate = null;
                LocalDate globalEndDate = null;

                // =====================================================
                // COSTRUZIONE NUOVA CONFIGURAZIONE
                // =====================================================

                for (BookingServiceRequestDto serviceRequest :
                        request.getServices()) {

                        if (serviceRequest.getServiceId() == null) {

                        throw new IllegalArgumentException(
                                "Servizio non specificato");
                        }

                        if (serviceRequest.getSelectedDates() == null ||
                                serviceRequest.getSelectedDates().isEmpty()) {

                        throw new IllegalArgumentException(
                                "Date mancanti");
                        }

                        // =================================================
                        // SERVIZIO
                        // =================================================

                        TeamService service =
                                serviceRepository.findById(
                                        serviceRequest.getServiceId()
                                ).orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Servizio non trovato"));

                        // =================================================
                        // DATE
                        // =================================================

                        List<LocalDate> dates =
                                new ArrayList<>(
                                        serviceRequest.getSelectedDates()
                                );

                        dates.sort(Comparator.naturalOrder());

                        LocalDate startDate =
                                dates.get(0);

                        LocalDate endDate =
                                dates.get(dates.size() - 1);

                        // =================================================
                        // DATE GLOBALI
                        // =================================================

                        if (globalStartDate == null ||
                                startDate.isBefore(globalStartDate)) {

                        globalStartDate = startDate;
                        }

                        if (globalEndDate == null ||
                                endDate.isAfter(globalEndDate)) {

                        globalEndDate = endDate;
                        }

                        // =================================================
                        // BOOKING SERVICE
                        // =================================================

                        da_figma.models.BookingService bookingService =
                                new da_figma.models.BookingService();

                        bookingService.setBooking(booking);
                        bookingService.setService(service);

                        bookingService.setServiceStartDate(startDate);
                        bookingService.setServiceEndDate(endDate);

                        bookingService.setDays(dates.size());

                        // =================================================
                        // PREZZO SERVIZIO
                        // =================================================

                        BigDecimal singleServicePrice =
                                pricingService.calculateSingleServicePrice(
                                        event,
                                        serviceRequest
                                );

                        bookingService.setPriceAtBooking(
                                singleServicePrice
                        );

                        newBookingServices.add(bookingService);

                        // =================================================
                        // MOTO
                        // =================================================

                        if (service.getPricingType()
                                == PricingType.BIKE_BASED) {

                        if (serviceRequest.getBikeId() == null) {

                                throw new IllegalArgumentException(
                                        "Moto obbligatoria per il servizio "
                                                + service.getName());
                        }

                        Bike bike =
                                bikeRepository.findByIdForUpdate(
                                        serviceRequest.getBikeId()
                                ).orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Moto non trovata"));


                        boolean available =
                                bikeRepository.isAvailableForEdit(
                                        bike.getId(),
                                        startDate,
                                        endDate,
                                        bookingId
                                );

                        if (!available) {

                                throw new IllegalArgumentException(
                                        "Moto non disponibile nel periodo selezionato");
                        }

                        BookingBikeAllocation allocation =
                                new BookingBikeAllocation();

                        allocation.setBooking(booking);
                        allocation.setBike(bike);

                        allocation.setRentalStartDate(startDate);
                        allocation.setRentalEndDate(endDate);

                        newBikeAllocations.add(allocation);
                        }
                }

                // =====================================================
                // DATE GLOBALI
                // =====================================================

                if (globalStartDate == null ||
                        globalEndDate == null) {

                        throw new IllegalArgumentException(
                                "Periodo prenotazione non valido");
                }

                // =====================================================
                // SOSTITUZIONE SERVIZI
                // =====================================================

                booking.getBookingServices().clear();

                for (da_figma.models.BookingService service :
                        newBookingServices) {

                        service.setBooking(booking);

                        booking.getBookingServices().add(service);
                }

                // =====================================================
                // SOSTITUZIONE MOTO
                // =====================================================

                booking.getBikeAllocations().clear();

                for (BookingBikeAllocation allocation : newBikeAllocations) {

                        allocation.setBooking(booking);

                        booking.getBikeAllocations().add(allocation);
                }

                // =====================================================
                // DATI PRENOTAZIONE
                // =====================================================

                booking.setEvent(event);

                booking.setBookingStartDate(globalStartDate);

                booking.setBookingEndDate(globalEndDate);

                booking.setTotalPrice(totalPrice);


                // =====================================================
                // CREDITO GENERATO DALLA MODIFICA
                // =====================================================

                BigDecimal creditAmount = BigDecimal.ZERO;

                if (differenceAmount != null && differenceAmount.compareTo(BigDecimal.ZERO) < 0) {

                        creditAmount = differenceAmount.abs();
                }

                booking.setCreditAmount(creditAmount);


                // =====================================================
                // STATO PAGAMENTO
                // =====================================================

                if (paymentConfirmed) {

                        booking.setPaymentStatus("PAID");
                }


                return bookingRepository.save(booking);

        }

        @Transactional(readOnly = true)
        public BigDecimal calculateBookingModificationDifference(Long bookingId,
                                        BookingRequestDto request) {

                String keycloakId = userContextService.getCurrentKeycloakId();

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Prenotazione non trovata"));

                if (!booking.getUserKeycloakId().equals(keycloakId)) {

                        throw new IllegalArgumentException(
                                "Non puoi modificare questa prenotazione");
                }

                if (!booking.getEvent().getId().equals(request.getEventId())) {

                        throw new IllegalArgumentException(
                                "Non è possibile cambiare evento");
                }

                BigDecimal newTotal = pricingService.calculatePrice(request);

                BigDecimal alreadyPaid = BigDecimal.ZERO;

                if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {

                        alreadyPaid = booking.getTotalPrice();
                }

                return newTotal.subtract(alreadyPaid);
        }

        public BookingPriceDifferenceType determineBookingPriceDifference(
                                                        Long bookingId,
                                                        BookingRequestDto request) {

                BigDecimal difference = calculateBookingModificationDifference(
                                bookingId,
                                request
                        );

                if (difference.compareTo(BigDecimal.ZERO) > 0) {

                        return BookingPriceDifferenceType.INCREASE;
                }

                if (difference.compareTo(BigDecimal.ZERO) < 0) {

                        return BookingPriceDifferenceType.DECREASE;
                }

                return BookingPriceDifferenceType.NO_CHANGE;
        }

        @Transactional
        public Booking applyBookingCredit(
                                                Long bookingId,
                                                BookingRequestDto request,
                                                BigDecimal differenceAmount) {

                String keycloakId = userContextService.getCurrentKeycloakId();

                return updateBookingInternal(
                        bookingId,
                        request,
                        keycloakId,
                        differenceAmount,
                        false
                );
        }

        @Transactional
        public Booking cancelMyBooking(Long bookingId) {

                String keycloakId = userContextService.getCurrentKeycloakId();

                // =====================================================
                // PRENOTAZIONE
                // =====================================================

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Prenotazione non trovata"));

                // =====================================================
                // SICUREZZA
                // =====================================================

                if (!booking.getUserKeycloakId().equals(keycloakId)) {

                        throw new IllegalArgumentException(
                                "Non puoi cancellare questa prenotazione");
                }

                // =====================================================
                // CONTROLLO STATO
                // =====================================================

                if (booking.getStatus() == BookingStatus.CANCELLED) {

                        throw new IllegalArgumentException(
                                "La prenotazione è già stata cancellata");
                }

                // =====================================================
                // CREDITO
                // =====================================================

                BigDecimal currentCredit = booking.getCreditAmount();

                if (currentCredit == null) {
                        currentCredit = BigDecimal.ZERO;
                }

                BigDecimal cancellationCredit = BigDecimal.ZERO;

                if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {

                        cancellationCredit = booking.getTotalPrice();
                }

                booking.setCreditAmount(
                        currentCredit.add(cancellationCredit)
                );

                // =====================================================
                // EVENTO
                // =====================================================

                Event event = booking.getEvent();

                if (booking.getStatus() == BookingStatus.CONFIRMED) {

                        eventService.unregisterBooking(event);
                }

                // =====================================================
                // LIBERA LA MOTO
                // =====================================================

                booking.getBikeAllocations().clear();

                // =====================================================
                // CANCELLA PRENOTAZIONE
                // =====================================================

                booking.setStatus(BookingStatus.CANCELLED);

                // =====================================================
                // SALVATAGGIO
                // =====================================================

                return bookingRepository.save(booking);
        }



}