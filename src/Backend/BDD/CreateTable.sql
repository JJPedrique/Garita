-- Creación de Tablas

CREATE TABLE "usuarios" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "clave" varchar(16) NOT NULL,
  "rol" varchar(20) NOT NULL,
  "nombre" varchar(20) NOT NULL,
  "apellido" varchar(20) NOT NULL,
  "cedula" varchar(13) UNIQUE NOT NULL,
  "telefono" varchar(13) NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE "bitacoras" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "usuario" varchar(13) NOT NULL,
  "accion" varchar(6) NOT NULL,
  "tabla_modificada" varchar(16) NOT NULL,
  "fecha_modificacion" timestamp NOT NULL
);

CREATE TABLE "viviendas" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "calle" varchar(20) NOT NULL,
  "numero_vivienda" varchar(10) UNIQUE NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE "representantes" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "id_vivienda" integer NOT NULL,
  "nombre" varchar(20) NOT NULL,
  "apellido" varchar(20) NOT NULL,
  "cedula" varchar(13) UNIQUE NOT NULL,
  "telefono" varchar(13) NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE "carnets" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "codigo" varchar(10) UNIQUE NOT NULL,
  "id_vivienda" integer NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE "cuotas" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "monto" numeric(6,2) NOT NULL,
  "descripcion" varchar(14) UNIQUE NOT NULL,
  "fecha_emision" timestamp NOT NULL,
  "fecha_limite" timestamp NOT NULL,
  "activo" bool NOT NULL DEFAULT true
);

CREATE TABLE "pagos_realizados" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "id_vivienda" integer NOT NULL,
  "id_cuota" integer NOT NULL,
  "tipo_pago" varchar(15) NOT NULL,
  "referencia" varchar(30),
  "fecha_de_pago" timestamp NOT NULL
);

CREATE TABLE "accesos" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "fecha_hora" timestamp NOT NULL,
  "tipo" varchar(10) NOT NULL,
  "estado" varchar(10) NOT NULL,
  "id_carnet" integer,
  "nombre_visita" varchar(40)
);

-- Comentarios en Columnas

COMMENT ON COLUMN "usuarios"."cedula" IS 'Candidata';

COMMENT ON COLUMN "bitacoras"."usuario" IS 'Candidata';

COMMENT ON COLUMN "viviendas"."numero_vivienda" IS 'Candidata';

COMMENT ON COLUMN "representantes"."cedula" IS 'Candidata';

COMMENT ON COLUMN "carnets"."codigo" IS 'Candidata';

COMMENT ON COLUMN "cuotas"."descripcion" IS 'Candidata';

-- Restricciones de Llave Foránea (Foreign Keys)

ALTER TABLE "accesos" ADD FOREIGN KEY ("id_carnet") REFERENCES "carnets" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "representantes" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "carnets" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "pagos_realizados" ADD FOREIGN KEY ("id_vivienda") REFERENCES "viviendas" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "pagos_realizados" ADD FOREIGN KEY ("id_cuota") REFERENCES "cuotas" ("id") DEFERRABLE INITIALLY IMMEDIATE;
