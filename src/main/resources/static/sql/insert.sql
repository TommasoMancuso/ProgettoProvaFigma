-- INSERIMENTO RUOLI

INSERT INTO team_roles (name) VALUES
('Manager'),
('Capo meccanico'),
('Motorista'),
('Sospensionista'),
('Telemetrista'),
('Elettronico');


INSERT INTO circuits (name, country, length_meters, image, description, active) VALUES
(
    "Autodromo Internazionale del Mugello", 
    'Italia', 
    5245, 
    NULL, 
    'Situato a Scarperia, è di proprietà della Ferrari. Famoso per il suo lungo rettilineo di oltre 1.1 km e per ospitare storicamente il Gran Premio d''Italia del Motomondiale.', 
    TRUE
),
(
    'Misano World Circuit Marco Simoncelli', 
    'Italia', 
    4226, 
    NULL, 
    'Situato a Misano Adriatico, è intitolato alla memoria di Marco Simoncelli. Ospita regolarmente il Gran Premio di San Marino e della Riviera di Rimini di MotoGP e il round di Superbike.', 
    TRUE
),
(
    'Autodromo Internazionale Enzo e Dino Ferrari di Imola', 
    'Italia', 
    4909, 
    NULL, 
    'Situato a Imola, è un circuito storico dal layout tecnico e affascinante che ruota in senso antiorario. Ospita importanti eventi motoristici a due e quattro ruote di livello mondiale.', 
    TRUE
),
(
    "Autodromo dell'Umbria Magione", 
    'Italia', 
    2507, 
    NULL, 
    'Situato a Magione, in provincia di Perugia, è un autodromo storico di caratura nazionale che ospita regolarmente gare del CIV e svariati trofei motociclistici italiani.', 
    TRUE
);

