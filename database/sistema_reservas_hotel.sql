-- SISTEMA DE RESERVAS DE HOTEL
-- Ejecutar sobre una base creada con: CREATE DATABASE sistema_reservas_hotel;

DROP TABLE IF EXISTS consumos_servicio;
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS servicios;
DROP TABLE IF EXISTS habitaciones;
DROP TABLE IF EXISTS huespedes;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL
);

CREATE TABLE huespedes (
    id_huesped SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE habitaciones (
    id_habitacion SERIAL PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    capacidad INTEGER NOT NULL,
    precio_noche NUMERIC(10,2) NOT NULL,

    CONSTRAINT chk_habitaciones_tipo
        CHECK (tipo IN ('SIMPLE', 'DOBLE', 'MATRIMONIAL', 'SUITE')),

    CONSTRAINT chk_habitaciones_capacidad
        CHECK (capacidad > 0),

    CONSTRAINT chk_habitaciones_precio
        CHECK (precio_noche > 0)
);

CREATE TABLE servicios (
    id_servicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,

    CONSTRAINT chk_servicios_precio
        CHECK (precio > 0)
);

CREATE TABLE reservas (
    id_reserva SERIAL PRIMARY KEY,
    id_huesped INTEGER NOT NULL,
    id_habitacion INTEGER NOT NULL,
    fecha_ingreso DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    temporada VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    costo_hospedaje NUMERIC(12,2) NOT NULL DEFAULT 0,
    total NUMERIC(12,2) NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,

    CONSTRAINT fk_reservas_huespedes
        FOREIGN KEY (id_huesped)
        REFERENCES huespedes(id_huesped),

    CONSTRAINT fk_reservas_habitaciones
        FOREIGN KEY (id_habitacion)
        REFERENCES habitaciones(id_habitacion),

    CONSTRAINT chk_reservas_fechas
        CHECK (fecha_salida > fecha_ingreso),

    CONSTRAINT chk_reservas_temporada
        CHECK (temporada IN ('BAJA', 'REGULAR', 'ALTA')),

    CONSTRAINT chk_reservas_total
        CHECK (total >= 0 AND costo_hospedaje >= 0),

    CONSTRAINT chk_reservas_estado
        CHECK (
            estado IN (
                'PENDIENTE',
                'CONFIRMADA',
                'EN_CURSO',
                'FINALIZADA',
                'CANCELADA'
            )
        )
);

CREATE TABLE consumos_servicio (
    id_consumo SERIAL PRIMARY KEY,
    id_reserva INTEGER NOT NULL,
    id_servicio INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,

    CONSTRAINT fk_consumos_reserva
        FOREIGN KEY (id_reserva)
        REFERENCES reservas(id_reserva)
        ON DELETE CASCADE,

    CONSTRAINT fk_consumos_servicio
        FOREIGN KEY (id_servicio)
        REFERENCES servicios(id_servicio),

    CONSTRAINT chk_consumos_cantidad
        CHECK (cantidad > 0),

    CONSTRAINT chk_consumos_subtotal
        CHECK (subtotal >= 0)
);


-- DATOS


INSERT INTO huespedes (nombre, documento, email, telefono) VALUES
('Rosa Quispe Huamán', '45879632', 'rosa.quispe@gmail.com', '956321478'),
('Jorge Salazar Peña', '71236548', 'jorge.salazar@gmail.com', '987412365'),
('Lucía Paredes Ríos', '70125896', 'lucia.paredes@gmail.com', '912345678'),
('Miguel Cárdenas Soto', '46985213', 'miguel.cardenas@gmail.com', '934567812'),
('Valeria Ochoa León', '72589631', 'valeria.ochoa@gmail.com', '945678123');

INSERT INTO habitaciones (numero, tipo, capacidad, precio_noche) VALUES
('101', 'SIMPLE', 1, 120.00),
('102', 'SIMPLE', 1, 120.00),
('201', 'DOBLE', 2, 180.00),
('202', 'MATRIMONIAL', 2, 220.00),
('301', 'SUITE', 4, 450.00);

INSERT INTO servicios (nombre, precio) VALUES
('Desayuno buffet', 35.00),
('Lavandería', 25.00),
('Spa y masajes', 120.00),
('Traslado aeropuerto', 60.00),
('Tour a las Líneas de Nasca', 250.00),
('Paseo en tubular - Huacachina', 80.00);

-- Hospedaje = noches * precio_noche * factor de temporada (BAJA 0.85, REGULAR 1.00, ALTA 1.25)
INSERT INTO reservas
(id_huesped, id_habitacion, fecha_ingreso, fecha_salida, temporada, costo_hospedaje, total, estado)
VALUES
(1, 3, '2026-10-20', '2026-10-23', 'REGULAR', 540.00, 610.00, 'FINALIZADA'),
(2, 5, '2026-10-24', '2026-10-26', 'ALTA', 1125.00, 1375.00, 'EN_CURSO'),
(3, 1, '2026-11-02', '2026-11-04', 'BAJA', 204.00, 204.00, 'CONFIRMADA'),
(4, 4, '2026-11-10', '2026-11-13', 'REGULAR', 660.00, 660.00, 'PENDIENTE'),
(5, 2, '2026-10-15', '2026-10-17', 'REGULAR', 240.00, 240.00, 'CANCELADA');

INSERT INTO consumos_servicio
(id_reserva, id_servicio, cantidad, subtotal)
VALUES
-- Rosa: 2 desayunos
(1, 1, 2, 70.00),
-- Jorge: tour a Nasca
(2, 5, 1, 250.00);

-- Usuario inicial (la contraseña se convierte a hash PBKDF2 en el primer inicio de sesión)
INSERT INTO usuarios (usuario, contrasena)
VALUES ('admin', 'admin')
ON CONFLICT (usuario) DO NOTHING;
