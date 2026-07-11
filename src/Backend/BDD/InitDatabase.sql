CREATE TABLE IF NOT EXISTS "usuarios" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "clave" varchar(16) NOT NULL,
  "rol" varchar(20) NOT NULL,
  "nombre" varchar(20) NOT NULL,
  "apellido" varchar(20) NOT NULL,
  "cedula" varchar(13) UNIQUE NOT NULL,
  "telefono" varchar(13) NOT NULL,
  "activo" bool NOT NULL DEFAULT true,
  "intentos_fallidos" integer DEFAULT 0
);

CREATE TABLE IF NOT EXISTS "bitacoras" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "usuario" varchar(13) NOT NULL,
  "accion" varchar(6) NOT NULL,
  "tabla_modificada" varchar(16) NOT NULL,
  "fecha_modificacion" timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS "viviendas" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "calle" varchar(20) NOT NULL,
  "numero_vivienda" varchar(10) UNIQUE NOT NULL,
  "fecha_registro" timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS "representantes" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "id_vivienda" int NOT NULL,
  "nombre" varchar(20) NOT NULL,
  "apellido" varchar(20) NOT NULL,
  "cedula" varchar(13) UNIQUE NOT NULL,
  "telefono" varchar(13) NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS "carnets" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "codigo" varchar(10) UNIQUE NOT NULL,
  "id_vivienda" int NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS "cuotas" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "monto" numeric(6,2) NOT NULL,
  "descripcion" varchar(14) UNIQUE NOT NULL,
  "fecha_emision" timestamp NOT NULL,
  "fecha_limite" timestamp NOT NULL,
  "activo" bool NOT NULL DEFAULT true,
  "borrada" bool NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS "pagos_realizados" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "id_vivienda" int NOT NULL,
  "id_cuota" int NOT NULL,
  "tipo_pago" varchar(15) NOT NULL,
  "referencia" varchar(30),
  "fecha_de_pago" timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS "accesos" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "fecha_hora" timestamp NOT NULL,
  "tipo" varchar(10) NOT NULL,
  "estado" varchar(10) NOT NULL,
  "id_carnet" int,
  "nombre_visita" varchar(40)
);

-- Comentarios de columnas
COMMENT ON COLUMN "usuarios"."cedula" IS 'Candidata';
COMMENT ON COLUMN "bitacoras"."usuario" IS 'Candidata';
COMMENT ON COLUMN "viviendas"."numero_vivienda" IS 'Candidata';
COMMENT ON COLUMN "representantes"."cedula" IS 'Candidata';
COMMENT ON COLUMN "carnets"."codigo" IS 'Candidata';
COMMENT ON COLUMN "cuotas"."descripcion" IS 'Candidata';

-- Llaves foráneas
ALTER TABLE "accesos" ADD FOREIGN KEY ("id_carnet") REFERENCES "carnets" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "representantes" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "carnets" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "pagos_realizados" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "pagos_realizados" ADD FOREIGN KEY ("id_cuota") REFERENCES "cuotas" ("id") DEFERRABLE INITIALLY IMMEDIATE;

-- 2. RESTRICCIONES DE LLAVE FORÁNEA (Solo si no existen)
-- Para evitar errores de duplicados, envolvemos cada una en una verificación rápida

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'accesos_id_carnet_fkey') THEN
        ALTER TABLE "accesos" ADD CONSTRAINT "accesos_id_carnet_fkey" FOREIGN KEY ("id_carnet") REFERENCES "carnets" ("id") DEFERRABLE INITIALLY IMMEDIATE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'representantes_id_vivienda_fkey') THEN
        ALTER TABLE "representantes" ADD CONSTRAINT "representantes_id_vivienda_fkey" FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'carnets_id_vivienda_fkey') THEN
        ALTER TABLE "carnets" ADD CONSTRAINT "carnets_id_vivienda_fkey" FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'pagos_realizados_id_vivienda_fkey') THEN
        ALTER TABLE "pagos_realizados" ADD CONSTRAINT "pagos_realizados_id_vivienda_fkey" FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'pagos_realizados_id_cuota_fkey') THEN
        ALTER TABLE "pagos_realizados" ADD CONSTRAINT "pagos_realizados_id_cuota_fkey" FOREIGN KEY ("id_cuota") REFERENCES "cuotas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
    END IF;
END $$;

-- 3. FUNCIONES (El 'OR REPLACE' ya se encarga de actualizarlas si existen)

CREATE OR REPLACE FUNCTION deuda(integer)
RETURNS DECIMAL(6,2)
AS $$
DECLARE 
	PAGADA DECIMAL(6,2);
	DEBE DECIMAL(6,2);
BEGIN
	SELECT SUM(cuotas.monto) INTO PAGADA
    from pagos_realizados
	LEFT JOIN cuotas ON cuotas.id = pagos_realizados.id_cuota 
    where id_vivienda = $1; 

    SELECT SUM(cuotas.id) INTO DEBE
    FROM cuotas; 

    RETURN PAGADA - DEBE;
END;
$$ 
LANGUAGE plpgsql;

-- OJO: Corregido un pequeño error de sintaxis en tu función original (usaba comillas dobles en "Solvente", deben ser simples 'Solvente')
CREATE OR REPLACE FUNCTION solvencia(integer)
RETURNS VARCHAR
AS $$
DECLARE 
	PAGOS_REALIZADOS INT;
	CUOTAS INT;
BEGIN
    SELECT Count(pagos_realizados.id) INTO PAGOS_REALIZADOS
    from pagos_realizados
    where id_vivienda = $1;

    SELECT Count(cuotas.id) INTO CUOTAS
    FROM cuotas; 

    IF PAGOS_REALIZADOS = CUOTAS THEN
        RETURN 'Solvente';
    ELSE 
        RETURN 'Moroso';
    END IF;
END;
$$ 
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION registrar_en_bitacora()
RETURNS TRIGGER AS $$
DECLARE
    v_usuario VARCHAR(50);
BEGIN
    v_usuario := current_setting('app.usuario_actual', true);
	
    IF v_usuario IS NULL OR v_usuario = '' THEN
        v_usuario := session_user;
    END IF;

    IF (TG_OP = 'DELETE') THEN
        INSERT INTO bitacoras (usuario, accion, tabla_modificada, fecha_modificacion)
        VALUES (v_usuario, TG_OP, TG_TABLE_NAME, NOW());
        RETURN OLD;
    ELSE
        INSERT INTO bitacoras (usuario, accion, tabla_modificada, fecha_modificacion)
        VALUES (v_usuario, TG_OP, TG_TABLE_NAME, NOW());
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


-- 4. TRIGGERS (Evaluamos si existen antes de crearlos)

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_usuarios') THEN
        CREATE TRIGGER trigger_auditoria_usuarios AFTER INSERT OR UPDATE OR DELETE ON usuarios FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_cuotas') THEN
        CREATE TRIGGER trigger_auditoria_cuotas AFTER INSERT OR UPDATE OR DELETE ON cuotas FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_viviendas') THEN
        CREATE TRIGGER trigger_auditoria_viviendas AFTER INSERT OR UPDATE OR DELETE ON viviendas FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_representantes') THEN
        CREATE TRIGGER trigger_auditoria_representantes AFTER INSERT OR UPDATE OR DELETE ON representantes FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_carnets') THEN
        CREATE TRIGGER trigger_auditoria_carnets AFTER INSERT OR UPDATE OR DELETE ON carnets FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_accesos') THEN
        CREATE TRIGGER trigger_auditoria_accesos AFTER UPDATE OR DELETE ON accesos FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_pagosrealizados') THEN
        CREATE TRIGGER trigger_auditoria_pagosrealizados AFTER UPDATE OR DELETE ON pagos_realizados FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_auditoria_bitacoras') THEN
        CREATE TRIGGER trigger_auditoria_bitacoras AFTER UPDATE OR DELETE ON bitacoras FOR EACH ROW EXECUTE FUNCTION registrar_en_bitacora();
    END IF;
END $$;