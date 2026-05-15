CREATE TABLE suscripciones (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               miembro_id BIGINT NOT NULL,
                               tipo_plan VARCHAR(50) NOT NULL,
                               fecha_inicio DATE NOT NULL,
                               fecha_fin DATE NOT NULL,
                               estado VARCHAR(20) NOT NULL,
                               precio DOUBLE NOT NULL
);