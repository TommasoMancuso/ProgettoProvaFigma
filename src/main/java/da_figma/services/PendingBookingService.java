package da_figma.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import da_figma.dtos.BookingRequestDto;
import da_figma.models.PendingBooking;
import da_figma.repositories.PendingBookingRepository;

@Service
public class PendingBookingService {

    private final PendingBookingRepository repository;
    private final ObjectMapper objectMapper;

    public PendingBookingService(
            PendingBookingRepository repository,
            ObjectMapper objectMapper) {

        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public PendingBooking create(String keycloakId, BookingRequestDto request, BigDecimal totalPrice) {

        try {

            PendingBooking pending = new PendingBooking();

            pending.setUserKeycloakId(keycloakId);

            pending.setBookingJson(objectMapper.writeValueAsString(request));

            pending.setTotalPrice(totalPrice);

            pending.setPaymentStatus("PENDING");

            return repository.save(pending);

        } catch (Exception e) {

            throw new RuntimeException("Errore creazione PendingBooking", e);
        }
    }

    public BookingRequestDto getRequest(PendingBooking pending) {

        try {

            return objectMapper.readValue(pending.getBookingJson(), BookingRequestDto.class);

        } catch (Exception e) {

            throw new RuntimeException("Errore lettura bookingJson", e);
        }
    }

    public PendingBooking save(PendingBooking pendingBooking) {
        return repository.save(pendingBooking);
    }
}