--
-- PostgreSQL database dump
--

\restrict xChLmsL6BOWhDO8QXyzilUqyebjtLn2THmc50vIXJ3mhcCr8ORoHW0t76f8wENJ

-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: deuda(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.deuda(idvivienda integer) RETURNS numeric
    LANGUAGE plpgsql
    AS $_$DECLARE
	MontoTotal decimal(6,2);
	MontoPagado decimal(6,2);
BEGIN
	SELECT SUM(cuotas.monto) INTO MontoTotal
	FROM cuotas;

	SELECT SUM(cuotas.monto) INTO MontoPagado
	FROM cuotas
	JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_cuota
	WHERE pagos_realizados.id_vivienda = $1;

	IF MontoPagado IS NULL THEN 
		return MontoTotal;
	END IF;
	
	return MontoTotal - MontoPagado;
END;

$_$;


ALTER FUNCTION public.deuda(idvivienda integer) OWNER TO postgres;

--
-- Name: solvencia(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.solvencia(idvivienda integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$

DECLARE 
    pagos integer; 
    cuotas integer;
BEGIN 

    SELECT COUNT(*) INTO pagos 
    FROM pagos_realizados 
    WHERE id_vivienda = $1; 

    SELECT COUNT(*) INTO cuotas 
    FROM cuotas; 

    IF cuotas = pagos THEN 
        RETURN 'SOLVENTE'; 
    ELSE 
        RETURN 'MOROSO'; 
    END IF; 
END;

$_$;


ALTER FUNCTION public.solvencia(idvivienda integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: accesos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.accesos (
    id integer NOT NULL,
    fecha_hora timestamp with time zone NOT NULL,
    tipo character varying(10) NOT NULL,
    estado character varying(10) NOT NULL,
    id_carnet integer,
    nombre_visita character varying(40)
);


ALTER TABLE public.accesos OWNER TO postgres;

--
-- Name: accesos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.accesos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.accesos_id_seq OWNER TO postgres;

--
-- Name: accesos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.accesos_id_seq OWNED BY public.accesos.id;


--
-- Name: bitacoras; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bitacoras (
    id integer NOT NULL,
    usuario character varying(13) NOT NULL,
    accion character varying(6) NOT NULL,
    tabla_modificada character varying(16) NOT NULL,
    fecha_modificacion timestamp with time zone NOT NULL
);


ALTER TABLE public.bitacoras OWNER TO postgres;

--
-- Name: COLUMN bitacoras.usuario; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.bitacoras.usuario IS 'Candidata';


--
-- Name: bitacoras_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bitacoras_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bitacoras_id_seq OWNER TO postgres;

--
-- Name: bitacoras_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bitacoras_id_seq OWNED BY public.bitacoras.id;


--
-- Name: carnets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.carnets (
    id integer NOT NULL,
    codigo character varying(10) NOT NULL,
    id_vivienda integer NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.carnets OWNER TO postgres;

--
-- Name: COLUMN carnets.codigo; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.carnets.codigo IS 'Candidata';


--
-- Name: carnets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.carnets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.carnets_id_seq OWNER TO postgres;

--
-- Name: carnets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.carnets_id_seq OWNED BY public.carnets.id;


--
-- Name: cuotas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cuotas (
    id integer NOT NULL,
    monto numeric(6,2) NOT NULL,
    descripcion character varying(14) NOT NULL,
    fecha_emision timestamp with time zone NOT NULL,
    fecha_limite timestamp with time zone NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.cuotas OWNER TO postgres;

--
-- Name: COLUMN cuotas.descripcion; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.cuotas.descripcion IS 'Candidata';


--
-- Name: cuotas_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cuotas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cuotas_id_seq OWNER TO postgres;

--
-- Name: cuotas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cuotas_id_seq OWNED BY public.cuotas.id;


--
-- Name: pagos_realizados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pagos_realizados (
    id integer NOT NULL,
    id_vivienda integer NOT NULL,
    id_cuota integer NOT NULL,
    tipo_pago character varying(15) NOT NULL,
    referencia character varying(30),
    fecha_de_pago timestamp with time zone
);


ALTER TABLE public.pagos_realizados OWNER TO postgres;

--
-- Name: pagos_realizados_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pagos_realizados_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pagos_realizados_id_seq OWNER TO postgres;

--
-- Name: pagos_realizados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pagos_realizados_id_seq OWNED BY public.pagos_realizados.id;


--
-- Name: representantes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.representantes (
    id integer NOT NULL,
    id_vivienda integer NOT NULL,
    nombre character varying(20) NOT NULL,
    apellido character varying(20) NOT NULL,
    cedula character varying(13) NOT NULL,
    telefono character varying(13) NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.representantes OWNER TO postgres;

--
-- Name: COLUMN representantes.cedula; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.representantes.cedula IS 'Candidata';


--
-- Name: representantes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.representantes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.representantes_id_seq OWNER TO postgres;

--
-- Name: representantes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.representantes_id_seq OWNED BY public.representantes.id;


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id integer NOT NULL,
    clave character varying(16) NOT NULL,
    nombre character varying(20) NOT NULL,
    apellido character varying(20) NOT NULL,
    cedula character varying(13) NOT NULL,
    telefono character varying(13) NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- Name: COLUMN usuarios.cedula; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.usuarios.cedula IS 'Candidata';


--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: viviendas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.viviendas (
    id integer NOT NULL,
    categoria character varying(20) NOT NULL,
    numero_vivienda character varying(10) NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


ALTER TABLE public.viviendas OWNER TO postgres;

--
-- Name: COLUMN viviendas.numero_vivienda; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.viviendas.numero_vivienda IS 'Candidata';


--
-- Name: viviendas_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.viviendas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.viviendas_id_seq OWNER TO postgres;

--
-- Name: viviendas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.viviendas_id_seq OWNED BY public.viviendas.id;


--
-- Name: accesos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accesos ALTER COLUMN id SET DEFAULT nextval('public.accesos_id_seq'::regclass);


--
-- Name: bitacoras id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bitacoras ALTER COLUMN id SET DEFAULT nextval('public.bitacoras_id_seq'::regclass);


--
-- Name: carnets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carnets ALTER COLUMN id SET DEFAULT nextval('public.carnets_id_seq'::regclass);


--
-- Name: cuotas id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuotas ALTER COLUMN id SET DEFAULT nextval('public.cuotas_id_seq'::regclass);


--
-- Name: pagos_realizados id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos_realizados ALTER COLUMN id SET DEFAULT nextval('public.pagos_realizados_id_seq'::regclass);


--
-- Name: representantes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.representantes ALTER COLUMN id SET DEFAULT nextval('public.representantes_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: viviendas id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.viviendas ALTER COLUMN id SET DEFAULT nextval('public.viviendas_id_seq'::regclass);


--
-- Data for Name: accesos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.accesos (id, fecha_hora, tipo, estado, id_carnet, nombre_visita) FROM stdin;
1	2026-06-12 06:00:00-04	Entrada	Permitido	1	\N
2	2026-06-12 06:15:00-04	Entrada	Permitido	3	\N
3	2026-06-12 07:30:00-04	Entrada	Permitido	\N	Juan Carlos Hurtado (Delivery)
4	2026-06-12 07:45:00-04	Salida	Permitido	\N	Juan Carlos Hurtado (Delivery)
5	2026-06-12 08:00:00-04	Entrada	Denegado	7	\N
6	2026-06-12 08:30:00-04	Entrada	Permitido	4	\N
7	2026-06-12 09:10:00-04	Entrada	Permitido	\N	Sonia Estévez (Visita A-01)
8	2026-06-12 11:20:00-04	Salida	Permitido	1	\N
9	2026-06-12 12:00:00-04	Entrada	Permitido	8	\N
10	2026-06-12 13:15:00-04	Entrada	Denegado	\N	Desconocido sospechoso
11	2026-06-12 14:00:00-04	Salida	Permitido	3	\N
12	2026-06-12 15:30:00-04	Entrada	Permitido	11	\N
13	2026-06-12 16:45:00-04	Salida	Permitido	\N	Sonia Estévez (Visita A-01)
14	2026-06-12 18:00:00-04	Entrada	Permitido	2	\N
15	2026-06-12 19:15:00-04	Entrada	Permitido	\N	Médico Guardia (Emergencia C-02)
16	2026-06-12 20:30:00-04	Salida	Permitido	\N	Médico Guardia (Emergencia C-02)
17	2026-06-12 21:00:00-04	Salida	Permitido	4	\N
18	2026-06-12 22:00:00-04	Entrada	Permitido	5	\N
19	2026-06-12 23:30:00-04	Salida	Permitido	11	\N
20	2026-06-12 23:45:00-04	Entrada	Permitido	6	\N
\.


--
-- Data for Name: bitacoras; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bitacoras (id, usuario, accion, tabla_modificada, fecha_modificacion) FROM stdin;
1	V-15.432.109	INSERT	viviendas	2026-05-01 09:00:00-04
2	V-15.432.109	INSERT	representantes	2026-05-01 09:15:00-04
3	V-15.432.109	INSERT	cuotas	2026-05-01 10:00:00-04
4	V-18.765.432	UPDATE	carnets	2026-05-15 14:22:00-04
5	V-15.432.109	INSERT	cuotas	2026-06-01 08:05:00-04
6	V-20.111.222	INSERT	pagos_realizados	2026-06-05 11:30:00-04
7	V-20.111.222	INSERT	pagos_realizados	2026-06-06 14:10:00-04
8	V-18.765.432	UPDATE	viviendas	2026-06-10 16:45:00-04
9	V-15.432.109	INSERT	usuarios	2026-06-11 09:00:00-04
10	V-20.111.222	INSERT	pagos_realizados	2026-06-12 08:15:00-04
\.


--
-- Data for Name: carnets; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.carnets (id, codigo, id_vivienda, activo) FROM stdin;
1	TAG-000001	1	t
2	TAG-000002	1	t
3	TAG-000003	2	t
4	TAG-000004	3	t
5	TAG-000005	4	t
6	TAG-000006	5	t
7	TAG-000007	5	f
8	TAG-000008	6	t
9	TAG-000009	7	t
10	TAG-000010	8	t
11	TAG-000011	9	t
12	TAG-000012	9	t
\.


--
-- Data for Name: cuotas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cuotas (id, monto, descripcion, fecha_emision, fecha_limite, activo) FROM stdin;
1	50.00	Ene_Cond_2026	2026-01-01 08:00:00-04	2026-01-15 18:00:00-04	t
2	50.00	Feb_Cond_2026	2026-02-01 08:00:00-04	2026-02-15 18:00:00-04	t
3	60.00	Mar_Cond_2026	2026-03-01 08:00:00-04	2026-03-15 18:00:00-04	t
4	60.00	Abr_Cond_2026	2026-04-01 08:00:00-04	2026-04-15 18:00:00-04	t
5	120.00	Extra_Pint_26	2026-05-10 08:00:00-04	2026-06-10 18:00:00-04	t
\.


--
-- Data for Name: pagos_realizados; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pagos_realizados (id, id_vivienda, id_cuota, tipo_pago, referencia, fecha_de_pago) FROM stdin;
1	1	1	Transferencia	REF-20260105-991	2026-01-05 10:30:00-04
2	1	2	Transferencia	REF-20260204-812	2026-02-04 14:15:00-04
3	2	1	Pago Movil	PMR-77312	2026-01-10 19:22:00-04
4	3	1	Efectivo	\N	2026-01-12 11:00:00-04
5	3	2	Efectivo	\N	2026-02-14 09:45:00-04
6	4	1	Transferencia	REF-88123	2026-01-15 17:55:00-04
7	5	1	Zelle	Z-BATCH-9921	2026-01-02 08:10:00-04
8	5	2	Zelle	Z-BATCH-9988	2026-02-02 08:05:00-04
9	5	3	Zelle	Z-BATCH-1022	2026-03-02 09:00:00-04
10	6	1	Pago Movil	PMR-90012	2026-01-14 22:10:00-04
11	7	1	Transferencia	REF-001239	2026-01-15 16:30:00-04
12	8	1	Pago Movil	PMR-44321	2026-01-06 13:00:00-04
13	9	1	Zelle	Z-VIP-01	2026-01-05 12:00:00-04
14	9	2	Zelle	Z-VIP-02	2026-02-05 12:00:00-04
15	9	3	Zelle	Z-VIP-03	2026-03-05 12:00:00-04
\.


--
-- Data for Name: representantes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.representantes (id, id_vivienda, nombre, apellido, cedula, telefono, activo) FROM stdin;
1	1	Andrés	Salazar	V-11.222.333	0414-1111111	t
2	2	María	Contreras	V-14.444.555	0424-2222222	t
3	3	Ricardo	Peña	V-10.666.777	0412-3333333	t
4	4	Gabriela	Blanco	V-13.888.999	0416-4444444	t
5	5	Javier	Delgado	V-9.999.000	0426-5555555	t
6	6	Patricia	Rivas	V-12.333.444	0414-6666666	t
7	7	Fernando	Castillo	V-15.555.666	0424-7777777	t
8	8	Diana	Morales	V-17.777.888	0412-8888888	t
9	9	Alejandro	Urdaneta	V-8.444.222	0416-9999999	t
10	10	Manual	López	V-19.111.000	0414-0000000	f
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuarios (id, clave, nombre, apellido, cedula, telefono, activo) FROM stdin;
1	Admin2026*_!	Carlos	Mendoza	V-15.432.109	0414-1234567	t
2	Seguridad_Gar1	Luis	Rodríguez	V-18.765.432	0424-7654321	t
3	GaritaPass99	Ana	Martínez	V-20.111.222	0412-9998877	t
4	ClaveTemporal1	Pedro	Gómez	V-12.888.777	0416-5554433	f
5	Vigilante33	Marcos	Pérez	V-16.555.444	0414-3332211	t
\.


--
-- Data for Name: viviendas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.viviendas (id, categoria, numero_vivienda, activo) FROM stdin;
1	Familiar Standard	A-01	t
2	Familiar Standard	A-02	t
3	Familiar Premium	B-01	t
4	Familiar Premium	B-02	t
5	Townhouse VIP	C-01	t
6	Townhouse VIP	C-02	t
7	Apartamento	D-101	t
8	Apartamento	D-102	t
9	Apartamento VIP	E-201	t
10	Familiar Standard	A-03	f
\.


--
-- Name: accesos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.accesos_id_seq', 20, true);


--
-- Name: bitacoras_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bitacoras_id_seq', 10, true);


--
-- Name: carnets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.carnets_id_seq', 12, true);


--
-- Name: cuotas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.cuotas_id_seq', 5, true);


--
-- Name: pagos_realizados_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pagos_realizados_id_seq', 15, true);


--
-- Name: representantes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.representantes_id_seq', 10, true);


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 5, true);


--
-- Name: viviendas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.viviendas_id_seq', 10, true);


--
-- Name: accesos accesos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accesos
    ADD CONSTRAINT accesos_pkey PRIMARY KEY (id);


--
-- Name: bitacoras bitacoras_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bitacoras
    ADD CONSTRAINT bitacoras_pkey PRIMARY KEY (id);


--
-- Name: carnets carnets_codigo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carnets
    ADD CONSTRAINT carnets_codigo_key UNIQUE (codigo);


--
-- Name: carnets carnets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carnets
    ADD CONSTRAINT carnets_pkey PRIMARY KEY (id);


--
-- Name: cuotas cuotas_descripcion_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuotas
    ADD CONSTRAINT cuotas_descripcion_key UNIQUE (descripcion);


--
-- Name: cuotas cuotas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuotas
    ADD CONSTRAINT cuotas_pkey PRIMARY KEY (id);


--
-- Name: pagos_realizados pagos_realizados_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos_realizados
    ADD CONSTRAINT pagos_realizados_pkey PRIMARY KEY (id);


--
-- Name: representantes representantes_cedula_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.representantes
    ADD CONSTRAINT representantes_cedula_key UNIQUE (cedula);


--
-- Name: representantes representantes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.representantes
    ADD CONSTRAINT representantes_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_cedula_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_cedula_key UNIQUE (cedula);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: viviendas viviendas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.viviendas
    ADD CONSTRAINT viviendas_pkey PRIMARY KEY (id);


--
-- Name: accesos accesos_id_carnet_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accesos
    ADD CONSTRAINT accesos_id_carnet_fkey FOREIGN KEY (id_carnet) REFERENCES public.carnets(id) DEFERRABLE;


--
-- Name: carnets carnets_id_vivienda_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.carnets
    ADD CONSTRAINT carnets_id_vivienda_fkey FOREIGN KEY (id_vivienda) REFERENCES public.viviendas(id) DEFERRABLE;


--
-- Name: pagos_realizados pagos_realizados_id_cuota_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos_realizados
    ADD CONSTRAINT pagos_realizados_id_cuota_fkey FOREIGN KEY (id_cuota) REFERENCES public.cuotas(id) DEFERRABLE;


--
-- Name: pagos_realizados pagos_realizados_id_vivienda_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pagos_realizados
    ADD CONSTRAINT pagos_realizados_id_vivienda_fkey FOREIGN KEY (id_vivienda) REFERENCES public.viviendas(id) DEFERRABLE;


--
-- Name: representantes representantes_id_vivienda_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.representantes
    ADD CONSTRAINT representantes_id_vivienda_fkey FOREIGN KEY (id_vivienda) REFERENCES public.viviendas(id) DEFERRABLE;


--
-- PostgreSQL database dump complete
--

\unrestrict xChLmsL6BOWhDO8QXyzilUqyebjtLn2THmc50vIXJ3mhcCr8ORoHW0t76f8wENJ

