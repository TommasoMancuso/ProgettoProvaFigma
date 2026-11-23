package da_figma.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import da_figma.models.BookingBikeAllocation;

@Repository
public interface BookingBikeAllocationRepository extends JpaRepository<BookingBikeAllocation, Long> {

    // =========================
    // FIND ALL ALLOCATIONS FOR BIKE
    // =========================
    List<BookingBikeAllocation> findByBikeId(Long bikeId);

    // =========================
    // FIND ALL BOOKINGS FOR BOOKING
    // =========================
    List<BookingBikeAllocation> findByBookingId(Long bookingId);

    // =========================
    // CHECK OVERLAP (CORE LOGIC)
    // =========================
    @Query("""
        SELECT CASE WHEN COUNT(bba) > 0 THEN true ELSE false END
        FROM BookingBikeAllocation bba
        WHERE bba.bike.id = :bikeId
        AND NOT (
            bba.rentalEndDate < :startDate
            OR bba.rentalStartDate > :endDate
        )
    """)
    boolean existsOverlap(
            @Param("bikeId") Long bikeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =========================
    // FIND OVERLAPPING BOOKINGS
    // (utile per debug/admin)
    // =========================
    @Query("""
        SELECT bba
        FROM BookingBikeAllocation bba
        WHERE bba.bike.id = :bikeId
        AND NOT (
            bba.rentalEndDate < :startDate
            OR bba.rentalStartDate > :endDate
        )
    """)
    List<BookingBikeAllocation> findOverlappingAllocations(
            @Param("bikeId") Long bikeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =========================
    // FIND ALL ACTIVE ALLOCATIONS IN PERIOD
    // =========================
    @Query("""
        SELECT bba
        FROM BookingBikeAllocation bba
        WHERE NOT (
            bba.rentalEndDate < :startDate
            OR bba.rentalStartDate > :endDate
        )
    """)
    List<BookingBikeAllocation> findAllInPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}