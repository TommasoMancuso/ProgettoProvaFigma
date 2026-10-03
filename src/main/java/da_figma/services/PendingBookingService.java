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

    // =====================================================
    // NUOVA PRENOTAZIONE
    // =====================================================

    public PendingBooking create(
            String keycloakId,
            BookingRequestDto request,
            BigDecimal totalPrice) {

        try {

            PendingBooking pending = new PendingBooking();

            pending.setUserKeycloakId(keycloakId);

            // Nuova prenotazione
            pending.setBookingId(null);

            pending.setOperationType("CREATE");

            pending.setBookingJson(
                    objectMapper.writeValueAsString(request)
            );

            // Prezzo della nuova prenotazione
            pending.setTotalPrice(totalPrice);

            // Nessun vecchio prezzo
            pending.setOriginalTotalPrice(null);

            // Per una nuova prenotazione
            // la differenza coincide con il prezzo totale
            pending.setDifferenceAmount(totalPrice);

            pending.setPaymentStatus("PENDING");

            return repository.save(pending);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Errore creazione PendingBooking",
                    e
            );
        }
    }

    // =====================================================
    // MODIFICA PRENOTAZIONE
    // =====================================================

    public PendingBooking createModification(
            String keycloakId,
            Long bookingId,
            BookingRequestDto request,
            BigDecimal originalTotalPrice,
            BigDecimal newTotalPrice,
            BigDecimal differenceAmount) {

        try {

            PendingBooking pending = new PendingBooking();

            pending.setUserKeycloakId(keycloakId);

            // Prenotazione esistente
            pending.setBookingId(bookingId);

            pending.setOperationType("UPDATE");

            pending.setBookingJson(
                    objectMapper.writeValueAsString(request)
            );

            // Prezzo della nuova configurazione
            pending.setTotalPrice(newTotalPrice);

            // Prezzo precedente
            pending.setOriginalTotalPrice(originalTotalPrice);

            // Differenza:
            // positiva = pagamento richiesto
            // zero = nessun pagamento
            // negativa = credito
            pending.setDifferenceAmount(differenceAmount);

            pending.setPaymentStatus("PENDING");

            return repository.save(pending);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Errore creazione PendingBooking modifica",
                    e
            );
        }
    }

    // =====================================================
    // JSON → REQUEST
    // =====================================================

    public BookingRequestDto getRequest(
            PendingBooking pending) {

        try {

            return objectMapper.readValue(
                    pending.getBookingJson(),
                    BookingRequestDto.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Errore lettura bookingJson",
                    e
            );
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    public PendingBooking save(
            PendingBooking pendingBooking) {

        return repository.save(pendingBooking);
    }
}