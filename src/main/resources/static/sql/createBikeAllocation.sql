CREATE TABLE booking_bike_allocations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    booking_id BIGINT NOT NULL,
    bike_id BIGINT NOT NULL,

    rental_start_date DATE NOT NULL,
    rental_end_date DATE NOT NULL,

    CONSTRAINT fk_bba_booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(id),

    CONSTRAINT fk_bba_bike
        FOREIGN KEY (bike_id)
        REFERENCES bikes(id)
);