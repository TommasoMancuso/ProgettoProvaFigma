package da_figma.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import da_figma.models.Bike;
import da_figma.repositories.BikeRepository;

@Service
public class BikeAvailabilityService {

    @Autowired
    private BikeRepository bikeRepository;

    public List<Bike> getAvailableBikes(LocalDate start, LocalDate end) {
    return bikeRepository.findAvailableBikes(start, end);
}
}