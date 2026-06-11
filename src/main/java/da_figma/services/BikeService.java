package da_figma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import da_figma.dtos.BikeDto;
import da_figma.enums.BikeStatus;
import da_figma.models.Bike;
import da_figma.repositories.BikeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BikeService {

    private final BikeRepository bikeRepository;
    private final SupabaseStorageService storageService;

    @Value("${supabase.public-url}")
    private String supabasePublicUrl;

    // =========================
    // LISTA MOTO ATTIVE
    // =========================
    @Transactional(readOnly = true)
    public List<Bike> getAllActiveBikes() {

        return bikeRepository.findByStatusNot(BikeStatus.ARCHIVED);
    }

    // =========================
    // LISTA MOTO ARCHIVIATE
    // =========================
    @Transactional(readOnly = true)
    public List<Bike> getArchivedBikes() {

        return bikeRepository.findByStatus(BikeStatus.ARCHIVED);
    }

    // =========================
    // RICERCA SINGOLA MOTO
    // =========================
    @Transactional(readOnly = true)
    public Bike getBikeById(Long id) {

        return bikeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Moto non trovata"));
    }

    // =========================
    // CREAZIONE
    // =========================
    public Bike createBike(
            BikeDto dto,
            MultipartFile imageFile) {

        if (bikeRepository.existsByChassisNumber(dto.getChassisNumber())) {

            throw new RuntimeException(
                    "Esiste già una moto con questo numero di telaio");
        }

        Bike bike = new Bike();

        bike.setBrand(dto.getBrand());
        bike.setModel(dto.getModel());
        bike.setYear(dto.getYear());
        bike.setCategory(dto.getCategory());

        bike.setChassisNumber(dto.getChassisNumber());
        bike.setCurrentKm(dto.getCurrentKm());
        bike.setDailyPrice(dto.getDailyPrice());

        bike.setStatus(BikeStatus.AVAILABLE);

        bike.setBikeImage(resolveBikeImage(imageFile));

        return bikeRepository.save(bike);
    }

    // =========================
    // MODIFICA
    // =========================
    public Bike updateBike(
            Long id,
            BikeDto dto,
            MultipartFile imageFile) {

        Bike bike = getBikeById(id);

        // controllo numero telaio
        if (!bike.getChassisNumber().equals(dto.getChassisNumber())
                && bikeRepository.existsByChassisNumber(dto.getChassisNumber())) {

            throw new RuntimeException(
                    "Esiste già una moto con questo numero di telaio");
        }

        bike.setBrand(dto.getBrand());
        bike.setModel(dto.getModel());
        bike.setYear(dto.getYear());
        bike.setCategory(dto.getCategory());

        bike.setChassisNumber(dto.getChassisNumber());
        bike.setCurrentKm(dto.getCurrentKm());
        bike.setDailyPrice(dto.getDailyPrice());

        // nuova immagine caricata
        if (imageFile != null && !imageFile.isEmpty()) {

            deleteOldImageIfNeeded(bike);

            String imageUrl = storageService.uploadFile(imageFile);

            bike.setBikeImage(imageUrl);
        }

        return bikeRepository.save(bike);
    }

    // =========================
    // SOFT DELETE
    // =========================
    public void archiveBike(Long id) {

        Bike bike = getBikeById(id);

        bike.setStatus(BikeStatus.ARCHIVED);

        bikeRepository.save(bike);
    }

    // =========================
    // RIPRISTINO
    // =========================
    public void restoreBike(Long id) {

        Bike bike = getBikeById(id);

        bike.setStatus(BikeStatus.AVAILABLE);

        bikeRepository.save(bike);
    }

    // =========================
    // METODI PRIVATI
    // =========================

    private String resolveBikeImage(MultipartFile file) {

        if (file != null && !file.isEmpty()) {

            return storageService.uploadFile(file);
        }

        return supabasePublicUrl + "/defaultMoto.jpg";
    }

    private void deleteOldImageIfNeeded(Bike bike) {

        if (bike.getBikeImage() == null ||
                bike.getBikeImage().isBlank()) {

            return;
        }

        String defaultImage =
                supabasePublicUrl + "/defaultMoto.jpg";

        if (!bike.getBikeImage().equals(defaultImage)) {

            storageService.deleteFile(
                    bike.getBikeImage());
        }
    }
}