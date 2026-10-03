package da_figma.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import da_figma.dtos.BikeOptionDto;
import da_figma.dtos.BookingFormDto;
import da_figma.dtos.ServiceBookingDto;
import da_figma.enums.BikeStatus;
import da_figma.enums.PricingType;
import da_figma.models.Bike;
import da_figma.models.Event;
import da_figma.models.EventServicePrice;
import da_figma.models.TeamService;
import da_figma.repositories.BikeRepository;
import da_figma.repositories.EventRepository;
import da_figma.repositories.EventServicePriceRepository;
import da_figma.repositories.TeamServiceRepository;

@Service
public class BookingFacadeService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private EventServicePriceRepository eventServicePriceRepository;

    @Autowired
    private TeamServiceRepository teamServiceRepository;


    // =========================================================
    // NUOVA PRENOTAZIONE
    // =========================================================

    public BookingFormDto buildBookingForm(Long eventId) {

        return buildBookingFormInternal(eventId, null);
    }


    // =========================================================
    // MODIFICA PRENOTAZIONE ESISTENTE
    // =========================================================

    public BookingFormDto buildBookingFormForEdit(
            Long eventId,
            Long bookingId) {

        return buildBookingFormInternal(eventId, bookingId);
    }


    // =========================================================
    // METODO INTERNO COMUNE
    // =========================================================

    private BookingFormDto buildBookingFormInternal(
                                        Long eventId,
                                        Long bookingId) {

        // =====================================================
        // EVENTO
        // =====================================================

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Evento non trovato"));

        BookingFormDto dto = new BookingFormDto();


        // =====================================================
        // DATI EVENTO
        // =====================================================

        dto.setEventId(event.getId());

        dto.setEventName(event.getName());

        dto.setOrganizer(event.getOrganizer());

        dto.setCircuitName(event.getCircuit().getName());

        dto.setStartDate(event.getStartDate());

        dto.setEndDate(event.getEndDate());


        // =====================================================
        // DATE EVENTO
        // =====================================================

        List<LocalDate> eventDates = new ArrayList<>();

        LocalDate current = event.getStartDate();

        while (!current.isAfter(event.getEndDate())) {

                eventDates.add(current);

                current = current.plusDays(1);
        }

        dto.setEventDates(eventDates);


        // =====================================================
        // MOTO DISPONIBILI
        // =====================================================

        List<Bike> availableBikes;

        if (bookingId == null) {

                // NUOVA PRENOTAZIONE

                availableBikes = bikeRepository.findAvailableBikes(
                                event.getStartDate(),
                                event.getEndDate()
                        );

        } else {

                // MODIFICA PRENOTAZIONE

                availableBikes = bikeRepository.findAvailableBikesForEdit(
                                event.getStartDate(),
                                event.getEndDate(),
                                bookingId
                        );
        }


        // =====================================================
        // SERVIZI
        // =====================================================

        List<ServiceBookingDto> services = new ArrayList<>();

        List<EventServicePrice> eventServices = eventServicePriceRepository.findByEventId(eventId);


        for (EventServicePrice eventServicePrice : eventServices) {

                TeamService service = eventServicePrice.getService();


                // =================================================
                // SERVIZIO
                // =================================================

                ServiceBookingDto serviceDto = new ServiceBookingDto();


                serviceDto.setServiceId( service.getId());

                serviceDto.setServiceName( service.getName());

                serviceDto.setPricingType( service.getPricingType());


                // =================================================
                // PREZZI
                // =================================================

                if (service.getPricingType() == PricingType.FIXED) {

                        serviceDto.setPriceFullEvent(service.getBasePrice());
                }


                else if (service.getPricingType() == PricingType.EVENT_BASED) {

                        serviceDto.setPriceOneDay(eventServicePrice.getPriceOneDay());

                        serviceDto.setPriceTwoDays(eventServicePrice.getPriceTwoDays());

                        serviceDto.setPriceThreeDays(eventServicePrice.getPriceThreeDays());

                        serviceDto.setPriceFullEvent(eventServicePrice.getPriceFullEvent());
                }


                // =================================================
                // BIKE BASED
                // =================================================

                if (service.getPricingType() == PricingType.BIKE_BASED) {

                        List<BikeOptionDto> bikeDtos = new ArrayList<>();


                        for (Bike bike : availableBikes) {

                                // -----------------------------------------
                                // IGNORA MOTO NON DISPONIBILI
                                // -----------------------------------------

                                if (bike.getStatus() != BikeStatus.AVAILABLE) {

                                        continue;
                                }


                                // -----------------------------------------
                                // DTO MOTO
                                // -----------------------------------------

                                BikeOptionDto bikeDto = new BikeOptionDto();

                                bikeDto.setId(bike.getId());

                                bikeDto.setName(bike.getBrand()
                                                + " "
                                                + bike.getModel()
                                                + " ("
                                                + bike.getYear()
                                                + ")"
                                );


                                bikeDto.setDailyPrice(bike.getDailyPrice());

                                bikeDtos.add(bikeDto);
                        }


                        serviceDto.setAvailableBikes(bikeDtos);
                }


                // =================================================
                // AGGIUNTA SERVIZIO
                // =================================================

                services.add(serviceDto);
        }


        // =====================================================
        // SERVIZI NEL DTO FINALE
        // =====================================================

        dto.setServices(services);


        return dto;
        }

}

