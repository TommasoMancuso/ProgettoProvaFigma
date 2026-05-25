CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    name VARCHAR(50) NOT NULL,

    UNIQUE (name)
);

CREATE TABLE circuits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    name VARCHAR(100) NOT NULL,
    country VARCHAR(100),
    length_km DECIMAL(5,2),

    description TEXT
);

CREATE TABLE bikes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,

    category VARCHAR(50) NOT NULL
);

CREATE TABLE services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    name VARCHAR(100) NOT NULL,

    description TEXT NOT NULL,

    pricing_type VARCHAR(20) NOT NULL,

    base_price DECIMAL(10,2) NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,

    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,

    phone VARCHAR(20) NOT NULL,

    birth_date DATE NOT NULL,

    bio TEXT,
    profile_image VARCHAR(255),
    instagram VARCHAR(100),

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    role_id BIGINT NOT NULL,

    UNIQUE (email),

    INDEX idx_users_role_id (role_id),

    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
);

CREATE TABLE events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    name VARCHAR(150) NOT NULL,

    circuit_id BIGINT NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    organizer VARCHAR(150),

    description TEXT,

    max_participants INT,

    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_events_circuits
        FOREIGN KEY (circuit_id)
        REFERENCES circuits(id)
);

CREATE TABLE bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    total_price DECIMAL(10,2) NULL,

    payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    cancellation_deadline DATE NULL,

    CONSTRAINT fk_bookings_users
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_bookings_events
        FOREIGN KEY (event_id)
        REFERENCES events(id)
);

CREATE TABLE booking_services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),

    booking_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,

    quantity INT NOT NULL DEFAULT 1,

    price_at_booking DECIMAL(10,2) NULL,

    CONSTRAINT fk_bs_booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(id),

    CONSTRAINT fk_bs_service
        FOREIGN KEY (service_id)
        REFERENCES services(id)
);

