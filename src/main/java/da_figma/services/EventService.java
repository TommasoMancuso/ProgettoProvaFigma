package da_figma.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import da_figma.models.Circuit;
import da_figma.models.Event;
import da_figma.repositories.CircuitRepository;
import da_figma.repositories.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CircuitRepository circuitRepository;

    @Transactional
    public Event createEvent(Event event, Long circuitId) {

        Circuit circuit = circuitRepository.findById(circuitId)
                .orElseThrow(() -> new RuntimeException("Circuito non trovato"));

        event.setCircuit(circuit);

        if (event.getStatus() == null || event.getStatus().isBlank()) {
            event.setStatus("AVAILABLE");
        }

        return eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getFutureEvents() {
        return eventRepository.findByStartDateAfter(LocalDate.now());
    }

    public List<Event> getPastEvents() {
        return eventRepository.findByEndDateBefore(LocalDate.now());
    }

    public List<Event> searchByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Event> searchByOrganizer(String organizer) {
        return eventRepository.findByOrganizerContainingIgnoreCase(organizer);
    }

    public List<Event> searchByCircuit(String circuitName) {
        return eventRepository.findByCircuit_NameContainingIgnoreCase(circuitName);
    }

    @Transactional
    public Event updateStatus(Long eventId, String status) {

        Event event = getEventById(eventId);
        event.setStatus(status);

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
