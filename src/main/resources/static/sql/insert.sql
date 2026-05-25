-- INSERIMENTO RUOLI

INSERT INTO roles (name) VALUES
('Manager'),
('Capo meccanico'),
('Motorista'),
('Sospensionista'),
('Telemetrista'),
('Elettronico');

-- INSERIMENTO UTENTI PER OGNI RUOLO

INSERT INTO users (
    name, surname, email, password, phone,
    birth_date, bio, profile_image, instagram,
    enabled, created_at, role_id
) VALUES

-- 1. Manager
('Luca', 'Ferrari', 'luca.ferrari@team.com', 'password123', '+39 333 111 2222',
 '1985-04-12', 'Team manager con esperienza nel motorsport internazionale.',
 NULL, 'luca.ferrari',
 TRUE, NOW(), 1),

-- 2. Capo meccanico
('Marco', 'Rossi', 'marco.rossi@team.com', 'password123', '+39 333 222 3333',
 '1980-09-21', 'Responsabile del reparto meccanico e coordinamento box.',
 NULL, 'marco.rossi',
 TRUE, NOW(), 2),

-- 3. Motorista
('Davide', 'Bianchi', 'davide.bianchi@team.com', 'password123', '+39 333 333 4444',
 '1990-06-15', 'Specialista motori e manutenzione power unit.',
 NULL, 'davide.bianchi',
 TRUE, NOW(), 3),

-- 4. Sospensionista
('Andrea', 'Conti', 'andrea.conti@team.com', 'password123', '+39 333 444 5555',
 '1987-11-03', 'Esperto assetto sospensioni e setup pista.',
 NULL, 'andrea.conti',
 TRUE, NOW(), 4),

-- 5. Telemetrista
('Simone', 'Galli', 'simone.galli@team.com', 'password123', '+39 333 555 6666',
 '1992-02-28', 'Analisi dati telemetrici e performance rider.',
 NULL, 'simone.galli',
 TRUE, NOW(), 5),

-- 6. Elettronico
('Francesco', 'Esposito', 'francesco.esposito@team.com', 'password123', '+39 333 666 7777',
 '1988-07-19', 'Gestione elettronica moto e mapping centralina.',
 NULL, 'francesco.esposito',
 TRUE, NOW(), 6);

--  INSERIMENTO CIRCUITI

 INSERT INTO circuits (name, country, length_km, description) VALUES
('Autodromo di Mugello', 'Italia', 5.25, 'Circuito tecnico e veloce tra i più importanti al mondo.'),

('Misano World Circuit Marco Simoncelli', 'Italia', 4.20, 'Sede di competizioni mondiali come MotoGP e SBK.'),

('Autodromo di Cremona', 'Italia', 3.70, 'Circuito moderno tecnico utilizzato per test e trackdays.'),

('Autodromo di Vallelunga', 'Italia', 4.10, 'Circuito tecnico vicino a Roma, molto usato per test e sviluppo moto.'),

('Autodromo di Imola', 'Italia', 4.90, 'Circuito storico tecnico e impegnativo con grande tradizione racing.'),

('Autodromo di Racalmuto', 'Italia', 2.45, 'Circuito siciliano utilizzato per trackday e allenamenti.');

-- INSERIMENTO BIKES

INSERT INTO bikes (brand, model, year, category) VALUES

('Yamaha', 'YZF-R1M Race Kit', 2026, 'Superbike'),

('Yamaha', 'YZF-R6 Track Edition', 2026, 'Supersport600'),

('Kawasaki', 'Ninja ZX-6R Track Edition', 2026, 'Supersport600');

-- INSERIMENTO SERVICES

INSERT INTO services (name, description, pricing_type, base_price, active) VALUES

('Preparazione moto', 'Setup completo moto cliente: sospensioni, elettronica, geometrie.', 'VARIABLE', NULL, TRUE),

('Assistenza trackdays', 'Supporto tecnico durante giornate in pista.', 'FIXED', 350.00, TRUE),

('Assistenza intero campionato', 'Assistenza completa per tutta la stagione racing.', 'VARIABLE', NULL, TRUE),

('Assistenza test precampionato', 'Supporto tecnico durante test invernali e pre-season.', 'FIXED', 500.00, TRUE),

('Noleggio moto', 'Noleggio moto da pista con setup pronto gara.', 'DAILY', 600.00, TRUE);

