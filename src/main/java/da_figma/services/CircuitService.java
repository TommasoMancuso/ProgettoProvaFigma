package da_figma.services;

import java.util.List;

import org.springframework.stereotype.Service;
import da_figma.models.Circuit;
import da_figma.repositories.CircuitRepository;

@Service
public class CircuitService {

    private final CircuitRepository circuitRepository;

    public CircuitService(CircuitRepository circuitRepository) {
        this.circuitRepository = circuitRepository;
    }

    public List<Circuit> getActiveCircuits() {
        return circuitRepository.findByActiveTrue();
    }

    public Circuit getCircuitById(Long id) {
        return circuitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuito non trovato"));
    }

    // CREAZIONE CIRCUITO

    public Circuit createCircuit(Circuit circuit) {
        return circuitRepository.save(circuit);
    }

    // SALVATAGGIO CIRCUITO

    public Circuit updateCircuit(Long id, Circuit formCircuit) {

        Circuit circuit = getCircuitById(id);

        circuit.setName(formCircuit.getName());
        circuit.setCountry(formCircuit.getCountry());
        circuit.setLengthMeters(formCircuit.getLengthMeters());
        circuit.setDescription(formCircuit.getDescription());

        return circuitRepository.save(circuit);
    }

    public void archiveCircuit(Long id) {

        Circuit circuit = getCircuitById(id);

        circuit.setActive(false);

        circuitRepository.save(circuit);
    }

    public void restoreCircuit(Long id) {

        Circuit circuit = getCircuitById(id);

        circuit.setActive(true);

        circuitRepository.save(circuit);
    }
}