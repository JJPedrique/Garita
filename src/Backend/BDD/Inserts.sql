-- ==========================================
-- 1. TABLA: usuarios
-- ==========================================
INSERT INTO "usuarios" ("clave", "rol", "nombre", "apellido", "cedula", "telefono", "activo") VALUES
('adm@2026', 'Administrador', 'Carlos', 'Mendoza', 'V-15342890', '0414-1234567', true),
('sup#2026', 'Supervisor', 'Ana', 'Gomez', 'V-18456123', '0424-7654321', true),
('op1@2026', 'Operador', 'Luis', 'Rodriguez', 'V-20111222', '0416-5554433', true),
('op2@2026', 'Operador', 'Maria', 'Martinez', 'V-22333444', '0412-9998877', true),
('op3@2026', 'Operador', 'Pedro', 'Infante', 'V-25666777', '0426-1112233', false);

-- ==========================================
-- 2. TABLA: bitacoras
-- ==========================================
INSERT INTO "bitacoras" ("usuario", "accion", "tabla_modificada", "fecha_modificacion") VALUES
('V-15342890', 'INSERT', 'usuarios', '2026-01-01 08:00:00-04'),
('V-15342890', 'INSERT', 'viviendas', '2026-01-01 08:30:00-04'),
('V-18456123', 'INSERT', 'representantes', '2026-01-02 09:15:00-04'),
('V-18456123', 'INSERT', 'carnets', '2026-01-02 10:00:00-04'),
('V-20111222', 'INSERT', 'cuotas', '2026-01-05 14:02:00-04'),
('V-20111222', 'UPDATE', 'usuarios', '2026-02-10 11:20:00-04'),
('V-22333444', 'INSERT', 'accesos', '2026-03-01 06:00:00-04'),
('V-15342890', 'UPDATE', 'viviendas', '2026-04-15 16:45:00-04');

-- ==========================================
-- 3. TABLA: viviendas
-- ==========================================
INSERT INTO "viviendas" ("calle", "numero_vivienda", "activo") VALUES
('Calle Los Jabillos', 'A-10', true),
('Calle Los Jabillos', 'A-11', true),
('Calle Los Jabillos', 'A-12', true),
('Avenida Principal', 'B-01', true),
('Avenida Principal', 'B-02', true),
('Avenida Principal', 'B-03', true),
('Avenida Principal', 'B-04', true),
('Calle Las Flores', 'C-20', true),
('Calle Las Flores', 'C-21', true),
('Calle Las Flores', 'C-22', false);

-- ==========================================
-- 4. TABLA: representantes
-- ==========================================
INSERT INTO "representantes" ("id_vivienda", "nombre", "apellido", "cedula", "telefono", "activo") VALUES
(1, 'Pedro', 'Perez', 'V-10222333', '0414-1111111', true),
(2, 'Martha', 'Suarez', 'V-12444555', '0424-2222222', true),
(3, 'Jorge', 'Delgado', 'V-14666777', '0416-3333333', true),
(4, 'Elena', 'Blanco', 'V-16888999', '0412-4444444', true),
(5, 'Ricardo', 'Torres', 'V-11999888', '0414-5555555', true),
(6, 'Sofia', 'Castro', 'V-13555444', '0424-6666666', true),
(7, 'Manuel', 'Guanipa', 'V-17222111', '0416-7777777', true),
(8, 'Diana', 'Mendoza', 'V-19444333', '0412-8888888', true),
(9, 'Andres', 'Rios', 'V-21555666', '0426-9999999', false);

-- ==========================================
-- 5. TABLA: carnets
-- ==========================================
INSERT INTO "carnets" ("codigo", "id_vivienda", "activo") VALUES
('CRN-A10-1', 1, true),
('CRN-A10-2', 1, true),
('CRN-A11-1', 2, true),
('CRN-A12-1', 3, true),
('CRN-B01-1', 4, true),
('CRN-B02-1', 5, true),
('CRN-B03-1', 6, true),
('CRN-B04-1', 7, true),
('CRN-C20-1', 8, true),
('CRN-C21-1', 9, false);

-- ==========================================
-- 6. TABLA: cuotas
-- ==========================================
INSERT INTO "cuotas" ("monto", "descripcion", "fecha_emision", "fecha_limite", "activo") VALUES
(50.00, 'Enero 2026', '2026-01-01 00:00:00-04', '2026-01-15 23:59:59-04', true),
(50.00, 'Febrero 2026', '2026-02-01 00:00:00-04', '2026-02-15 23:59:59-04', true),
(50.00, 'Marzo 2026', '2026-03-01 00:00:00-04', '2026-03-15 23:59:59-04', true),
(60.00, 'Abril 2026', '2026-04-01 00:00:00-04', '2026-04-15 23:59:59-04', true),
(60.00, 'Mayo 2026', '2026-05-01 00:00:00-04', '2026-05-15 23:59:59-04', true),
(100.00, 'Extra Pintura', '2026-05-20 00:00:00-04', '2026-06-10 23:59:59-04', true);

-- ==========================================
-- 7. TABLA: pagos_realizados
-- ==========================================
INSERT INTO "pagos_realizados" ("id_vivienda", "id_cuota", "tipo_pago", "referencia", "fecha_de_pago") VALUES
-- Enero
(1, 1, 'Transferencia', 'REF-00101', '2026-01-05 10:00:00-04'),
(2, 1, 'Pago Móvil', 'PM-00102', '2026-01-06 11:30:00-04'),
(3, 1, 'Efectivo', NULL, '2026-01-10 16:00:00-04'),
(4, 1, 'Transferencia', 'REF-00104', '2026-01-12 09:15:00-04'),
(5, 1, 'Pago Móvil', 'PM-00105', '2026-01-14 20:00:00-04'),
-- Febrero
(1, 2, 'Transferencia', 'REF-00201', '2026-02-03 08:45:00-04'),
(2, 2, 'Pago Móvil', 'PM-00202', '2026-02-05 14:22:00-04'),
(3, 2, 'Efectivo', NULL, '2026-02-12 11:00:00-04'),
(6, 2, 'Transferencia', 'REF-00206', '2026-02-14 17:30:00-04'),
-- Marzo
(1, 3, 'Transferencia', 'REF-00301', '2026-03-02 09:00:00-04'),
(4, 3, 'Transferencia', 'REF-00304', '2026-03-05 13:12:00-04'),
(7, 3, 'Pago Móvil', 'PM-00307', '2026-03-10 18:25:00-04'),
-- Abril y Mayo
(2, 4, 'Pago Móvil', 'PM-00402', '2026-04-05 10:40:00-04'),
(5, 4, 'Pago Móvil', 'PM-00405', '2026-04-12 15:10:00-04'),
(1, 5, 'Transferencia', 'REF-00501', '2026-05-02 08:11:00-04'),
(3, 6, 'Efectivo', NULL, '2026-05-25 12:00:00-04');

-- ==========================================
-- 8. TABLA: accesos
-- ==========================================
INSERT INTO "accesos" ("fecha_hora", "tipo", "estado", "id_carnet", "nombre_visita") VALUES
-- Día 1
('2026-06-01 06:30:00-04', 'Entrada', 'Permitido', 1, NULL),
('2026-06-01 07:05:00-04', 'Entrada', 'Permitido', 3, NULL),
('2026-06-01 08:15:00-04', 'Entrada', 'Permitido', NULL, 'Jose Alana (Gas)'),
('2026-06-01 09:00:00-04', 'Salida', 'Permitido', 1, NULL),
('2026-06-01 11:30:00-04', 'Salida', 'Permitido', NULL, 'Jose Alana (Gas)'),
('2026-06-01 14:20:00-04', 'Entrada', 'Permitido', 5, NULL),
('2026-06-01 18:00:00-04', 'Entrada', 'Permitido', 6, NULL),
-- Día 2
('2026-06-02 06:12:00-04', 'Entrada', 'Permitido', 2, NULL),
('2026-06-02 07:45:00-04', 'Entrada', 'Permitido', 4, NULL),
('2026-06-02 10:00:00-04', 'Entrada', 'Denegado', 10, NULL), -- Carnet Inactivo (id 10)
('2026-06-02 12:15:00-04', 'Entrada', 'Permitido', NULL, 'Yoraco Mendoza (Visita A-10)'),
('2026-06-02 15:30:00-04', 'Salida', 'Permitido', 2, NULL),
-- Día 3
('2026-06-03 07:00:00-04', 'Entrada', 'Permitido', 7, NULL),
('2026-06-03 08:22:00-04', 'Entrada', 'Permitido', 8, NULL),
('2026-06-03 13:11:00-04', 'Entrada', 'Permitido', NULL, 'Camion MRW'),
('2026-06-03 13:40:00-04', 'Salida', 'Permitido', NULL, 'Camion MRW'),
('2026-06-03 21:45:00-04', 'Salida', 'Permitido', 7, NULL);