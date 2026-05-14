CREATE TABLE planes (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(255),
                        precio DECIMAL(10,2) NOT NULL,
                        duracion_meses INT NOT NULL
);

CREATE TABLE suscripciones (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               miembro_id BIGINT NOT NULL,
                               plan_id BIGINT NOT NULL,
                               fecha_inicio DATE NOT NULL,
                               fecha_fin DATE NOT NULL,
                               estado VARCHAR(20) NOT NULL, -- ACTIVA, VENCIDA, CANCELADA
                               FOREIGN KEY (plan_id) REFERENCES planes(id)
);

