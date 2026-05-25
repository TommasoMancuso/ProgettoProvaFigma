package da_figma.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.BookingRequestDto;
import da_figma.models.*;
import da_figma.repositories.*;

@Service
public class BookingManagerService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    // ==================================================
    // CREATE BOOKING COMPLETO
    // ==================================================

    @Transactional
    public Booking createFullBooking(BookingRequestDto request) {

        // 1. USER
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User non trovato"));

        // 2. EVENT
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event non trovato"));

        // 3. CREATE BOOKING BASE
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus("PENDING");
        booking.setPaymentStatus("UNPAID");

        booking = bookingRepository.save(booking);

        BigDecimal totalPrice = BigDecimal.ZERO;

        // ==================================================
        // 4. SERVICES
        // ==================================================
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {

            for (Long serviceId : request.getServiceIds()) {

                da_figma.models.Service service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service non trovato"));

                BookingService bs = new BookingService();
                bs.setBooking(booking);
                bs.setService(service);

                // snapshot prezzo
                bs.setPriceAtBooking(service.getBasePrice());

                bookingServiceRepository.save(bs);

                booking.getServices().add(bs);

                if (service.getBasePrice() != null) {
                    totalPrice = totalPrice.add(service.getBasePrice());
                }
            }
        }

        // ==================================================
        // 5. BIKE (OPZIONALE)
        // ==================================================
        if (request.getBikeId() != null) {

            Bike bike = bikeRepository.findById(request.getBikeId())
                    .orElseThrow(() -> new RuntimeException("Bike non trovata"));

            booking.setBike(bike);

            // prezzo base noleggio (placeholder logico)
            totalPrice = totalPrice.add(new BigDecimal("600"));
        }

        // ==================================================
        // 6. TOTAL PRICE
        // ==================================================
        booking.setTotalPrice(totalPrice);

        return bookingRepository.save(booking);
    }
}