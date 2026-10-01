package da_figma.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import da_figma.enums.BikeStatus;
import da_figma.models.Bike;
import jakarta.persistence.LockModeType;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    // =========================
    // LOCK SINGOLA BIKE (SAFE UPDATE)
    // =========================
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bike b WHERE b.id = :id")
    Optional<Bike> findByIdForUpdate(@Param("id") Long id);

    // =========================
    // DISPONIBILITÀ (LISTA)
    // =========================
    @Query("""
        SELECT b
        FROM Bike b
        WHERE b.id NOT IN (
            SELECT ba.bike.id
            FROM BookingBikeAllocation ba
            WHERE NOT (
                ba.rentalEndDate < :startDate
                OR ba.rentalStartDate > :endDate
            )
        )
    """)
    List<Bike> findAvailableBikes(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =========================
// DISPONIBILITÀ DURANTE MODIFICA
// =========================
@Query("""
    SELECT b
    FROM Bike b
    WHERE b.id NOT IN (
        SELECT ba.bike.id
        FROM BookingBikeAllocation ba
        WHERE ba.booking.id <> :bookingId
        AND NOT (
            ba.rentalEndDate < :startDate
            OR ba.rentalStartDate > :endDate
        )
    )
""")
List<Bike> findAvailableBikesForEdit(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("bookingId") Long bookingId
);


    // RICERCA MOTO ARCHIVIATE O NO

    List<Bike> findByStatusNot(BikeStatus status);

    List<Bike> findByStatus(BikeStatus status);

    // =========================
    // CHECK SINGOLA BIKE (USATA DAL SERVICE)
    // =========================
    @Query("""
        SELECT CASE WHEN COUNT(ba) = 0 THEN true ELSE false END
        FROM BookingBikeAllocation ba
        WHERE ba.bike.id = :bikeId
        AND NOT (
            ba.rentalEndDate < :startDate
            OR ba.rentalStartDate > :endDate
        )
    """)
    boolean isAvailable(
            @Param("bikeId") Long bikeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // RICERCA PER TELAIO
    boolean existsByChassisNumber(String chassisNumber);
}