package da_figma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.dtos.EventDto;
import da_figma.enums.EventStatus;
import da_figma.models.Circuit;
import da_figma.models.Event;
import da_figma.models.EventServicePrice;
import da_figma.models.TeamService;
import da_figma.repositories.CircuitRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.EventServicePriceRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CircuitRepository circuitRepository;

    @Autowired
    private TeamServiceRepository teamServiceRepository;

    @Autowired
    private EventServicePriceRepository eventServicePriceRepository;

    // =========================
    // READ (ADMIN)
    // =========================

    public List<Event> getAllEvents() {
        return eventRepository.findByStatusNot(EventStatus.CANCELLED);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
    }

    // =========================
    // CREATE EVENT (ADMIN)
    // =========================
    @Transactional
    public Event createEvent(EventDto dto) {

        Circuit circuit = circuitRepository.findById(dto.getCircuitId())
                .orElseThrow(() -> new RuntimeException("Circuito non trovato"));

        Event event = new Event();

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setOrganizer(dto.getOrganizer());

        event.setCircuit(circuit);

        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());

        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentParticipants(0);

        event.setStatus(
                dto.getStatus() != null
                        ? dto.getStatus()
                        : EventStatus.AVAILABLE
        );

        Event savedEvent = eventRepository.save(event);

        if (dto.getServiceIds() != null) {

            for (Long serviceId : dto.getServiceIds()) {

                TeamService service =
                        teamServiceRepository.findById(serviceId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Servizio non trovato"));

                EventServicePrice esp =
                        new EventServicePrice();

                esp.setEvent(savedEvent);
                esp.setService(service);

                eventServicePriceRepository.save(esp);
            }
        }

        return savedEvent;
    }

    // =========================
// UPDATE EVENT (ADMIN)
// =========================
@Transactional
public Event updateEvent(Long id, EventDto dto) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event non trovato"));

        Circuit circuit = circuitRepository.findById(dto.getCircuitId())
                .orElseThrow(() -> new RuntimeException("Circuito non trovato"));

        // =========================
        // AGGIORNA DATI EVENTO
        // =========================

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setOrganizer(dto.getOrganizer());
        event.setCircuit(circuit);
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setMaxParticipants(dto.getMaxParticipants());

        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
        }

        Event savedEvent = eventRepository.save(event);

        // =========================
        // AGGIORNA SERVIZI
        // =========================

        List<EventServicePrice> existingPrices =
                eventServicePriceRepository.findByEventId(id);

        // Elimina i servizi rimossi
        existingPrices.removeIf(price -> {

            boolean keep = dto.getServiceIds() != null
                    && dto.getServiceIds().contains(price.getService().getId());

            if (!keep) {
                eventServicePriceRepository.delete(price);
            }

            return !keep;
        });

        // Aggiunge solo i nuovi servizi
        if (dto.getServiceIds() != null) {

            for (Long serviceId : dto.getServiceIds()) {

                boolean alreadyExists = existingPrices.stream()
                        .anyMatch(p -> p.getService().getId().equals(serviceId));

                if (!alreadyExists) {

                    TeamService service = teamServiceRepository.findById(serviceId)
                            .orElseThrow(() ->
                                    new RuntimeException("Servizio non trovato"));

                    EventServicePrice esp = new EventServicePrice();

                    esp.setEvent(savedEvent);
                    esp.setService(service);

                    eventServicePriceRepository.save(esp);
                }
            }
        }

        return savedEvent;
    }

    // =========================
    // SOFT DELETE (ARCHIVE LOGIC)
    // =========================
    @Transactional
    public void archiveEvent(Long id) {

        Event event = getEventById(id);

        event.setStatus(EventStatus.CANCELLED);

        eventRepository.save(event);
    }

    // =====================================================
    // ================= BUSINESS LOGIC ====================
    // (il tuo codice invariato sotto)
    // =====================================================

    @Transactional
    public void registerBooking(Event event) {

        event.setCurrentParticipants(
            event.getCurrentParticipants() + 1
        );

        eventRepository.save(event);
    }

    @Transactional
    public void unregisterBooking(Event event) {

        int current = (event.getCurrentParticipants() == null)
                ? 0
                : event.getCurrentParticipants();

        event.setCurrentParticipants(Math.max(0, current - 1));

        evaluateStatus(event);

        eventRepository.save(event);
    }

    private void evaluateStatus(Event event) {

        if (event.getStatus() == EventStatus.CANCELLED) return;
        if (event.getStatus() == EventStatus.COMPLETED) return;

        if (event.getMaxParticipants() != null
                && event.getCurrentParticipants() >= event.getMaxParticipants()) {

            event.setStatus(EventStatus.SOLD_OUT);
        } else {
            event.setStatus(EventStatus.AVAILABLE);
        }
    }

    public void validateBooking(Event event) {

        if (event.getStatus() == EventStatus.CANCELLED)
            throw new RuntimeException("Evento annullato");

        if (event.getStatus() == EventStatus.COMPLETED)
            throw new RuntimeException("Evento terminato");

        if (event.getStatus() == EventStatus.SOLD_OUT)
            throw new RuntimeException("Evento al completo");
    }

    @Transactional
    public void completeEvent(Event event) {
        event.setStatus(EventStatus.COMPLETED);
        eventRepository.save(event);
    }

    @Transactional
    public void cancelEvent(Event event) {
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }
}