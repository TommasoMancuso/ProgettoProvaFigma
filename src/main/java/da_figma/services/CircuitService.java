package da_figma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import da_figma.models.Circuit;
import da_figma.repositories.CircuitRepository;

@Service
public class CircuitService {

    @Autowired
    private SupabaseStorageService storageService;

    @Value("${supabase.public-url}")
    private String supabasePublicUrl;


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

    public Circuit createCircuit(Circuit circuit, MultipartFile imageFile) {
        circuit.setImage(resolveCircuitImage(imageFile));
        return circuitRepository.save(circuit);
    }

    // SALVATAGGIO CIRCUITO

    public Circuit updateCircuit(Long id, Circuit formCircuit, MultipartFile imageFile) {

        Circuit circuit = getCircuitById(id);

        circuit.setName(formCircuit.getName());
        circuit.setCountry(formCircuit.getCountry());
        circuit.setLengthMeters(formCircuit.getLengthMeters());
        circuit.setDescription(formCircuit.getDescription());

        if (imageFile != null && !imageFile.isEmpty()) {

            if (circuit.getImage() != null
                    && !circuit.getImage().contains("default.png")) {

                storageService.deleteFile(circuit.getImage());
            }

            circuit.setImage(
                    storageService.uploadFile(imageFile));
        }

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

    private String resolveCircuitImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            return storageService.uploadFile(file);
        }

        return supabasePublicUrl + "/default.png";
    }
}