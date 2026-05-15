-- Suscripción Mensual Activa para el Miembro 1
INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (1, 'MENSUAL', '2026-05-01', '2026-06-01', 'ACTIVA', 29990.0);

-- Suscripción Anual Activa para el Miembro 2
INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (2, 'ANUAL', '2026-01-15', '2027-01-15', 'ACTIVA', 250000.0);

-- Suscripción Trimestral Vencida para el Miembro 3
INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (3, 'TRIMESTRAL', '2025-10-01', '2026-01-01', 'VENCIDA', 75000.0);