package da_figma.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import da_figma.enums.BookingDuration;
import da_figma.models.Event;

@Service
public class BookingDurationResolver {

    public BookingDuration resolve(
            Event event,
            List<LocalDate> selectedDates) {

        if (selectedDates == null || selectedDates.isEmpty()) {
            throw new IllegalArgumentException(
                    "Devi selezionare almeno una data");
        }

        // Ordino sempre le date
        selectedDates.sort(Comparator.naturalOrder());

        // Verifica che tutte le date siano comprese nell'evento
        for (LocalDate date : selectedDates) {

            if (date.isBefore(event.getStartDate())
                    || date.isAfter(event.getEndDate())) {

                throw new IllegalArgumentException(
                        "Data non compresa nell'evento");
            }
        }

        // Verifica consecutività
        for (int i = 0; i < selectedDates.size() - 1; i++) {

            long daysBetween = ChronoUnit.DAYS.between(
                    selectedDates.get(i),
                    selectedDates.get(i + 1));

            if (daysBetween != 1) {

                throw new IllegalArgumentException(
                        "Le date devono essere consecutive");
            }
        }

        // FULL EVENT
        if (selectedDates.getFirst().equals(event.getStartDate())
                && selectedDates.getLast().equals(event.getEndDate())) {

            return BookingDuration.FULL_EVENT;
        }

        return switch (selectedDates.size()) {

            case 1 -> BookingDuration.ONE_DAY;

            case 2 -> BookingDuration.TWO_DAYS;

            case 3 -> BookingDuration.THREE_DAYS;

            default -> throw new IllegalArgumentException(
                    "Durata non supportata");
        };
    }
}