-- INSERIMENTO RUOLI

INSERT INTO roles (name) VALUES
('Manager'),
('Capo meccanico'),
('Motorista'),
('Sospensionista'),
('Telemetrista'),
('Elettronico'),
('User');

-- INSERIMENTO UTENTI PER OGNI RUOLO

INSERT INTO users (
    name,
    surname,
    email,
    password,
    phone,
    birth_date,
    bio,
    enabled,
    created_at,
    role_id
) VALUES

-- =========================
-- MANAGER / ADMIN
-- =========================
(
    'Marco',
    'Rinaldi',
    'manager@teamracing.it',
    '$2a$10$managerpasswordhash',
    '+393331112233',
    '1985-03-15',
    'Team manager con oltre 15 anni di esperienza nel motorsport nazionale e internazionale. Coordina eventi, piloti e gestione tecnica del team.',
    TRUE,
    NOW(),
    1
),

-- =========================
-- CAPO MECCANICO
-- =========================
(
    'Luca',
    'Ferrari',
    'capomeccanico@teamracing.it',
    '$2a$10$capomeccanicohash',
    '+393334445566',
    '1982-07-10',
    'Capo meccanico specializzato nella preparazione e gestione moto da competizione per track day e campionati endurance.',
    TRUE,
    NOW(),
    2
),

-- =========================
-- MOTORISTA
-- =========================
(
    'Davide',
    'Moretti',
    'motorista@teamracing.it',
    '$2a$10$motoristahash',
    '+393337778899',
    '1990-01-22',
    'Tecnico motorista esperto in setup, revisione e ottimizzazione di motori supersportivi ad alte prestazioni.',
    TRUE,
    NOW(),
    3
),

-- =========================
-- SOSPENSIONISTA
-- =========================
(
    'Andrea',
    'Bianchi',
    'sospensioni@teamracing.it',
    '$2a$10$sospensionihash',
    '+393331234567',
    '1988-11-05',
    'Specialista sospensioni con esperienza su assetti pista personalizzati per piloti amatori e professionisti.',
    TRUE,
    NOW(),
    4
),

-- =========================
-- TELEMETRISTA
-- =========================
(
    'Simone',
    'Greco',
    'telemetria@teamracing.it',
    '$2a$10$telemetriahash',
    '+393339876543',
    '1992-09-17',
    'Analista dati e telemetrie, supporta il team nell’ottimizzazione delle performance in pista.',
    TRUE,
    NOW(),
    5
),

-- =========================
-- ELETTRONICO
-- =========================
(
    'Federico',
    'Conti',
    'elettronica@teamracing.it',
    '$2a$10$elettronicahash',
    '+393336665544',
    '1987-05-28',
    'Tecnico elettronico specializzato in centraline racing, controlli motore e sistemi acquisizione dati.',
    TRUE,
    NOW(),
    6
),

-- =========================
-- USER ESTERNO
-- =========================
(
    'Giulia',
    'Romano',
    'giulia.romano@email.com',
    '$2a$10$userhash',
    '+393332224466',
    '1998-12-03',
    NULL,
    TRUE,
    NOW(),
    7
);

-- Inserimento moto

INSERT INTO bikes (
    brand,
    model,
    year,
    category,
    chassis_number,
    current_km,
    daily_price,
    status
) VALUES

-- =========================
-- SUPERBIKE
-- =========================
(
    'Ducati',
    'Panigale V4 S',
    2026,
    'SUPERBIKE',
    'DUC-SBK-2026-0001',
    320,
    450.00,
    'AVAILABLE'
),

-- =========================
-- BIG SUPERSPORT
-- =========================
(
    'Ducati',
    'Panigale V2',
    2026,
    'BIG_SUPERSPORT',
    'DUC-BSS-2026-0001',
    280,
    320.00,
    'AVAILABLE'
),

-- =========================
-- SUPERSPORT 600
-- =========================
(
    'Yamaha',
    'YZF-R6 Race',
    2026,
    'SUPERSPORT_600',
    'YAM-SS6-2026-0001',
    410,
    260.00,
    'AVAILABLE'
),

-- =========================
-- SUPER TWINS
-- =========================
(
    'Aprilia',
    'RS 660 Trofeo',
    2026,
    'SUPER_TWINS',
    'APR-STW-2026-0001',
    190,
    240.00,
    'AVAILABLE'
),

-- =========================
-- SUPERSPORT 300
-- =========================
(
    'Kawasaki',
    'Ninja ZX-4RR',
    2026,
    'SUPERSPORT_300',
    'KAW-SS3-2026-0001',
    150,
    180.00,
    'AVAILABLE'
);

-- Inserimento Circuiti

INSERT INTO circuits (
    name,
    country,
    length_meters,
    description,
    created_at
) VALUES

-- =========================
-- MUGELLO
-- =========================
(
    'Autodromo Internazionale del Mugello',
    'Italia',
    5245,
    'Circuito toscano tra i più iconici del Motomondiale, famoso per i lunghi curvoni veloci e il rettilineo principale.',
    NOW()
),

-- =========================
-- MISANO
-- =========================
(
    'Misano World Circuit Marco Simoncelli',
    'Italia',
    4226,
    'Storico circuito romagnolo dedicato a Marco Simoncelli, sede di eventi internazionali motociclistici.',
    NOW()
),

-- =========================
-- VALLELUNGA
-- =========================
(
    'Autodromo Vallelunga Piero Taruffi',
    'Italia',
    4085,
    'Circuito tecnico situato vicino Roma, molto utilizzato per test, gare nazionali e attività racing.',
    NOW()
),

-- =========================
-- CREMONA
-- =========================
(
    'Cremona Circuit',
    'Italia',
    3768,
    'Circuito moderno e tecnico che negli ultimi anni è diventato riferimento per competizioni motociclistiche italiane.',
    NOW()
),

-- =========================
-- IMOLA
-- =========================
(
    'Autodromo Internazionale Enzo e Dino Ferrari',
    'Italia',
    4909,
    'Storico autodromo italiano con layout impegnativo e importanti eventi internazionali auto e moto.',
    NOW()
),

-- =========================
-- RACALMUTO
-- =========================
(
    'Autodromo Valle dei Templi',
    'Italia',
    4000,
    'Circuito siciliano utilizzato per track day, competizioni regionali e attività di allenamento motociclistico.',
    NOW()
);

-- Inserimento servizi

INSERT INTO services (
    name,
    description,
    pricing_type,
    base_price,
    active,
    created_at
) VALUES

-- =========================
-- PREPARAZIONE MOTO
-- =========================
(
    'Preparazione Moto',
    'Servizio completo di preparazione moto pista: controlli ciclistica, setup base, manutenzione pre-evento, verifica sicurezza e configurazione iniziale.',
    'MANUAL',
    NULL,
    TRUE,
    NOW()
),

-- =========================
-- NOLEGGIO MOTO
-- =========================
(
    'Noleggio Moto',
    'Servizio di noleggio moto pronto pista. Il prezzo varia in base al modello selezionato e alla durata del noleggio.',
    'BIKE_BASED',
    NULL,
    TRUE,
    NOW()
),

-- =========================
-- ASSISTENZA IN PISTA
-- =========================
(
    'Assistenza in Pista',
    'Supporto tecnico completo durante eventi e gare: gestione moto, setup, controllo pressioni, termocoperte, interventi rapidi e supporto paddock.',
    'EVENT_BASED',
    NULL,
    TRUE,
    NOW()
);

-- Inserimento due eventi

INSERT INTO events (
    name,
    description,
    organizer,
    circuit_id,
    start_date,
    end_date,
    max_participants,
    current_participants,
    status,
    created_at
) VALUES

-- =========================
-- EVENTO 1
-- TRACK DAY CON NOLEGGIO
-- =========================
(
    'Track Experience Ducati V4',
    'Giornata in pista dedicata al test e noleggio di moto supersportive con supporto tecnico del team.',
    'Team Racing Experience',
    1,
    '2026-04-18',
    '2026-04-18',
    20,
    0,
    'AVAILABLE',
    NOW()
),

-- =========================
-- EVENTO 2
-- ASSISTENZA MOTO PRIVATA
-- =========================
(
    'Weekend Racing Misano',
    'Weekend di assistenza tecnica in pista dedicato a piloti con moto privata.',
    'Team Racing Experience',
    2,
    '2026-06-13',
    '2026-06-14',
    30,
    0,
    'AVAILABLE',
    NOW()
);

-- Inserimento Prezzi eventi prenotati

INSERT INTO event_service_prices (
    event_id,
    service_id,
    price_one_day,
    price_two_days,
    price_full_event
) VALUES

-- =========================
-- EVENTO 1 (TRACK DAY + NOLEGGIO)
-- =========================

-- Preparazione Moto
(
    1,
    1,
    150.00,
    250.00,
    400.00
),

-- Noleggio Moto (base, poi dipende dalla bike dailyPrice)
(
    1,
    2,
    300.00,
    550.00,
    1000.00
),

-- Assistenza in pista
(
    1,
    3,
    200.00,
    350.00,
    600.00
),

-- =========================
-- EVENTO 2 (MOTO PRIVATA + ASSISTENZA)
-- =========================

-- Preparazione Moto
(
    2,
    1,
    120.00,
    200.00,
    350.00
),

-- Assistenza in pista
(
    2,
    3,
    220.00,
    400.00,
    700.00
);