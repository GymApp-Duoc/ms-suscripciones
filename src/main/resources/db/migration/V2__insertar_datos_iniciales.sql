DELETE FROM suscripciones WHERE miembro_id IN (1, 2, 3);

INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (1, 'MENSUAL', '2026-05-01', '2026-06-01', 'ACTIVA', 29990.0);

INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (2, 'ANUAL', '2026-01-15', '2027-01-15', 'ACTIVA', 250000.0);

INSERT INTO suscripciones (miembro_id, tipo_plan, fecha_inicio, fecha_fin, estado, precio)
VALUES (3, 'TRIMESTRAL', '2025-10-01', '2026-01-01', 'VENCIDA', 75000.0);