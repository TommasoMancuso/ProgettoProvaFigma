package da_figma.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.enums.EventStatus;
import da_figma.models.Event;
import da_figma.repositories.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    // =========================
    // BOOKING REGISTRATION
    // =========================
    @Transactional
    public void registerBooking(Event event) {

        event.setCurrentParticipants(
            event.getCurrentParticipants() + 1
        );

        eventRepository.save(event);
    }

    // =========================
    // BOOKING CANCELLATION
    // =========================
    @Transactional
    public void unregisterBooking(Event event) {

        int current = (event.getCurrentParticipants() == null)
                ? 0
                : event.getCurrentParticipants();

        event.setCurrentParticipants(Math.max(0, current - 1));

        evaluateStatus(event);

        eventRepository.save(event);
    }

    // =========================
    // STATUS ENGINE (SINGLE SOURCE OF TRUTH)
    // =========================
    private void evaluateStatus(Event event) {

        // blocco totale manuale
        if (event.getStatus() == EventStatus.CANCELLED) {
            return;
        }

        // evento completato manualmente
        if (event.getStatus() == EventStatus.COMPLETED) {
            return;
        }

        // sold out logic
        if (event.getMaxParticipants() != null
                && event.getCurrentParticipants() >= event.getMaxParticipants()) {

            event.setStatus(EventStatus.SOLD_OUT);
        } else {
            event.setStatus(EventStatus.AVAILABLE);
        }
    }

    // =========================
    // BOOKING VALIDATION
    // =========================
    public void validateBooking(Event event) {

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new RuntimeException("Evento annullato");
        }

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new RuntimeException("Evento terminato");
        }

        if (event.getStatus() == EventStatus.SOLD_OUT) {
            throw new RuntimeException("Evento al completo");
        }
    }

    // =========================
    // ADMIN: COMPLETE EVENT
    // =========================
    @Transactional
    public void completeEvent(Event event) {

        event.setStatus(EventStatus.COMPLETED);
        eventRepository.save(event);
    }

    // =========================
    // ADMIN: CANCEL EVENT
    // =========================
    @Transactional
    public void cancelEvent(Event event) {

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }
}