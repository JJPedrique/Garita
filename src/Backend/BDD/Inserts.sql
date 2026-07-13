DO $$
DECLARE
    i INT;
    vivienda_id INT;
    cuota_id INT;
    carnet_id INT;
BEGIN
    -- ==========================================
    -- 1. POBLAR TABLA: usuarios (10 filas)
    -- ==========================================
    FOR i IN 1..10 LOOP
        INSERT INTO "usuarios" ("clave", "rol", "nombre", "apellido", "cedula", "telefono", "activo", "intentos_fallidos")
        VALUES (
            'pass' || (1000 + i),
            CASE WHEN i = 1 THEN 'Administrador' ELSE 'Operador' END,
            'Usuario_Nom_' || i,
            'Usuario_Ape_' || i,
            'V-' || (10000000 + i), -- Cédulas únicas
            '0414' || (1000000 + i),
            true,
            0
        );
    END LOOP;

    -- ==========================================
    -- 2. POBLAR TABLA: bitacoras (15 filas)
    -- ==========================================
    FOR i IN 1..15 LOOP
        INSERT INTO "bitacoras" ("usuario", "accion", "tabla_modificada", "fecha_modificacion")
        VALUES (
            'V-' || (10000000 + (1 + (i % 5))), -- Asocia a los primeros usuarios
            CASE WHEN i % 3 = 0 THEN 'INSERT' WHEN i % 3 = 1 THEN 'UPDATE' ELSE 'DELETE' END,
            CASE WHEN i % 2 = 0 THEN 'viviendas' ELSE 'usuarios' END,
            NOW() - (i || ' hours')::INTERVAL
        );
    END LOOP;

    -- ==========================================
    -- 3. POBLAR TABLA: viviendas (15 filas)
    -- ==========================================
    FOR i IN 1..15 LOOP
        INSERT INTO "viviendas" ("calle", "numero_vivienda", "fecha_registro", "activo")
        VALUES (
            CASE WHEN i <= 5 THEN 'Calle A' WHEN i <= 10 THEN 'Calle B' ELSE 'Avenida Principal' END,
            'VIV-' || (100 + i), -- Códigos únicos de vivienda
            NOW() - (i || ' days')::INTERVAL,
            true
        );
    END LOOP;

    -- ==========================================
    -- 4. POBLAR TABLA: cuotas (5 filas)
    -- ==========================================
    FOR i IN 1..5 LOOP
        INSERT INTO "cuotas" ("monto", "descripcion", "fecha_emision", "fecha_limite", "activo", "borrada")
        VALUES (
            20.00 + (i * 5.50),
            'Condominio M' || (10 + i), -- Descripciones únicas obligatorias
            NOW() - (i || ' months')::INTERVAL,
            NOW() - (i || ' months')::INTERVAL + INTERVAL '15 days',
            true,
            false
        );
    END LOOP;

END $$;


DO $$
DECLARE
    i INT;
    v_id INT;
    c_id INT;
    carnet_id INT;
    rec RECORD;
BEGIN
    -- ==========================================
    -- 5. POBLAR TABLA: representantes (15 filas)
    -- Asigna un representante a cada una de las 15 viviendas creadas
    -- ==========================================
    i := 1;
    FOR rec IN SELECT id FROM "viviendas" ORDER BY id LOOP
        INSERT INTO "representantes" ("id_vivienda", "nombre", "apellido", "cedula", "telefono", "activo")
        VALUES (
            rec.id,
            'Repr_Nom_' || i,
            'Repr_Ape_' || i,
            'V-' || (20000000 + i), -- Cédulas únicas para representantes
            '0412' || (2000000 + i),
            true
        );
        i := i + 1;
    END LOOP;

    -- ==========================================
    -- 6. POBLAR TABLA: carnets (20 filas)
    -- Asigna entre 1 y 2 carnets por vivienda distribuidos
    -- ==========================================
    i := 1;
    FOR rec IN SELECT id FROM "viviendas" ORDER BY id LOOP
        -- Primer carnet obligatorio por vivienda
        INSERT INTO "carnets" ("codigo", "id_vivienda", "activo")
        VALUES ('CRN-' || (1000 + i), rec.id, true);
        i := i + 1;
        
        -- Algunas viviendas reciben un segundo carnet (familias grandes)
        IF rec.id % 2 = 0 THEN
            INSERT INTO "carnets" ("codigo", "id_vivienda", "activo")
            VALUES ('CRN-' || (2000 + i), rec.id, true);
            i := i + 1;
        END IF;
    END LOOP;

    -- ==========================================
    -- 7. POBLAR TABLA: pagos_realizados (25 filas)
    -- Cruza viviendas con cuotas simulando un historial de pagos
    -- ==========================================
    i := 1;
    FOR v_id IN SELECT id FROM "viviendas" LOOP
        FOR c_id IN SELECT id FROM "cuotas" LOOP
            -- Simulamos que no todos han pagado todo, solo un historial cruzado
            IF (v_id + c_id) % 3 != 0 THEN
                INSERT INTO "pagos_realizados" ("id_vivienda", "id_cuota", "tipo_pago", "referencia", "fecha_de_pago")
                VALUES (
                    v_id,
                    c_id,
                    CASE WHEN i % 2 = 0 THEN 'Transferencia' ELSE 'Pago Movil' END,
                    'REF-XYZ-' || (5000 + i),
                    NOW() - (i || ' days')::INTERVAL
                );
                i := i + 1;
            END IF;
        END LOOP;
    END LOOP;

    -- ==========================================
    -- 8. POBLAR TABLA: accesos (30 filas)
    -- Registra movimientos usando carnets existentes y visitas casuales
    -- ==========================================
    i := 1;
    FOR rec IN SELECT id FROM "carnets" LIMIT 15 LOOP
        -- Registro de entrada para el carnet
        INSERT INTO "accesos" ("fecha_hora", "tipo", "estado", "id_carnet", "nombre_visita")
        VALUES (NOW() - (i || ' hours')::INTERVAL, 'Entrada', 'Permitido', rec.id, NULL);
        
        -- Registro de salida correspondiente unas horas después
        INSERT INTO "accesos" ("fecha_hora", "tipo", "estado", "id_carnet", "nombre_visita")
        VALUES (NOW() - (i || ' hours')::INTERVAL + INTERVAL '2 hours', 'Salida', 'Permitido', rec.id, NULL);
        
        i := i + 1;
    END LOOP;

    -- Agregar accesos extra para personas externas (visitantes sin carnet)
    FOR i IN 1..10 LOOP
        INSERT INTO "accesos" ("fecha_hora", "tipo", "estado", "id_carnet", "nombre_visita")
        VALUES (
            NOW() - (i || ' days')::INTERVAL,
            'Entrada',
            CASE WHEN i = 7 THEN 'Denegado' ELSE 'Permitido' END, -- Simulamos un acceso denegado
            NULL,
            'Visitante Eventual Extra #' || i
        );
    END LOOP;

END $$;