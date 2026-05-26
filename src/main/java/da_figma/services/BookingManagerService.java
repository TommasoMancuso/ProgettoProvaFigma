package da_figma.services;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.BookingRequestDto;
import da_figma.enums.BookingStatus;
import da_figma.models.*;
import da_figma.repositories.*;

@Service
public class BookingManagerService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private BikeRepository bikeRepository;
    @Autowired private TeamServiceRepository serviceRepository;
    @Autowired private EventServicePriceRepository eventServicePriceRepository;
    @Autowired private BookingBikeAllocationRepository bookingBikeAllocationRepository;
    @Autowired private EventService eventService;

    // =========================
    // CREATE BOOKING
    // =========================
    @Transactional
    public Booking createBooking(BookingRequestDto request) {

        // =========================
        // 1. USER
        // =========================
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User non trovato"));

        // =========================
        // 2. EVENT (PESSIMISTIC LOCK)
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
        booking.setUser(user);
        booking.setEvent(event);
        booking.setBike(bike);
        booking.setBookingServices(new ArrayList<>());
        booking.setStatus(BookingStatus.PENDING);

        // =========================
        // 5. SERVICES + QUOTE SYSTEM
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
        // 7. BIKE ALLOCATION (ONLY IF CONFIRMED)
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