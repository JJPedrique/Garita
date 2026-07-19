--LISTA DE MOROSOS
SELECT viviendas.calle,viviendas.numero_vivienda,
		(
		SELECT CONCAT(representantes.nombre,' ',representantes.apellido)
		FROM representantes
		WHERE representantes.id_vivienda = viviendas.id
		ORDER BY representantes.id
		LIMIT 1
		) as Representante,
		solvencia(viviendas.id) AS Estado,
		deuda(viviendas.id) AS Debe
FROM viviendas
ORDER BY estado ASC

--BALANCE (?)
WITH ResumenFinanciero AS (
    SELECT cuotas.descripcion as Cuota, 
           cuotas.monto * (SELECT COUNT(id) FROM viviendas) as deuda,
           cuotas.monto * COUNT(pagos_realizados.id) as ingreso
    FROM cuotas
    JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_vivienda
    GROUP BY cuotas.id, cuotas.descripcion, cuotas.monto
)

SELECT Cuota, -deuda, ingreso, (ingreso - deuda) as Balance
FROM ResumenFinanciero
UNION
SELECT  'TOTAL', -SUM(deuda), SUM(ingreso), SUM((ingreso - deuda)) 
FROM ResumenFinanciero;


--MODULOS

-- OBTENER COLUMNAS Y TIPOS
CREATE OR REPLACE TEMP VIEW vista_analisis_columnas AS

SELECT viviendas.calle AS Calle,
 		viviendas.numero_vivienda AS Vivienda, 
 		carnets.codigo AS Codigo
FROM carnets
JOIN viviendas ON viviendas.id = carnets.id_vivienda;


SELECT 
    column_name AS columna_nombre,
    data_type AS tipo_base,
    COALESCE(domain_name, data_type) AS tipo_dominio,
    character_maximum_length AS longitud_maxima
FROM information_schema.columns
WHERE table_name = 'vista_analisis_columnas'
ORDER BY ordinal_position;

DROP VIEW vista_analisis_columnas;


--VIVIENDAS
SELECT viviendas.calle AS Calle, 
		viviendas.numero_vivienda AS Vivienda , 
		COUNT(representantes.id) AS  Representantes, 
		COUNT(carnets.id) AS Carntes
FROM viviendas
JOIN representantes ON viviendas.id = representantes.id_vivienda
JOIN carnets ON viviendas.id = carnets.id_vivienda
GROUP BY viviendas.calle, viviendas.numero_vivienda

--REPRESENTANTES
SELECT viviendas.calle AS Calle, 
		viviendas.numero_vivienda AS Vivienda,
		representantes.nombre AS Nombre,
		representantes.apellido AS Apellido,
		representantes.cedula AS Cedula,
		representantes.telefono AS Telefono  
FROM representantes
JOIN viviendas ON viviendas.id = representantes.id_vivienda 

--CARNETS
SELECT viviendas.calle AS Calle,
 		viviendas.numero_vivienda AS Vivienda, 
 		carnets.codigo AS Codigo
FROM carnets
JOIN viviendas ON viviendas.id = carnets.id_vivienda 

--REGISTRO DE ACCESOS (MUCHOS NULL)
SELECT accesos.fecha_hora AS "Fecha de Acceso",
 		accesos.tipo AS Tipo,
		accesos.estado AS Estado, 
 		viviendas.calle AS Calle, 
 		viviendas.numero_vivienda AS Vivienda, 
 		carnets.codigo AS Codigo, 
 		accesos.nombre_visita AS Visita
FROM accesos
LEFT JOIN carnets ON carnets.id = accesos.id_carnet
LEFT JOIN viviendas ON viviendas.id = carnets.id_vivienda

--CUOTAS
SELECT cuotas.descripcion AS Descripcion,
		cuotas.fecha_emision AS "Fecha de Emision",
 		cuotas.monto AS Monto, 
 		cuotas.fecha_limite AS "Fecha Limite de pago", 
		COUNT(pagos_realizados.id) AS "Cuotas Pagadas",
		(SELECT COUNT(*) FROM viviendas) - COUNT(pagos_realizados.id) AS "Cuotas No Pagadas"
FROM cuotas
LEFT JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_cuota
GROUP BY descripcion,fecha_emision, monto, fecha_limite

--PAGOS REALIZADOS
SELECT viviendas.calle AS Calle,
		viviendas.numero_vivienda AS Vivienda, 
		cuotas.monto AS Monto,
		cuotas.descripcion AS Descripcion, 
		pagos_realizados.tipo_pago AS "Tipo de Pago",
		pagos_realizados.referencia AS Referencia,
		pagos_realizados.fecha_de_pago AS "Fecha de Pago"
FROM pagos_realizados
JOIN cuotas ON cuotas.id = pagos_realizados.id_cuota
JOIN viviendas ON viviendas.id = pagos_realizados.id_vivienda

--BITACORA
SELECT bitacoras.usuario AS Usuario,
	bitacoras.accion AS Accion,
	bitacoras.tabla_modificada AS "Tabla Modificada",
	bitacoras.fecha_modificacion AS "Fecha de Modificacion"
FROM bitacoras

--USUARIOS

/*----------------------------------------------------------------------------------------------------
	REGISTRO
----------------------------------------------------------------------------------------------------*/

/*

Reportes
Listado de Vivienda - Vivienda + Quien es su propietario?
Listado de Representante - Vivienda + Quienes son su representantes ?
Listado de Carnets - Vivienda + Cuales Carnets tiene asociados ?

- Lista de accesos por Carnets
- Lista de accesos por Visita

- Listado Coutas - Listado de Cuotas + Cuanto han Ganado? + Cuantos Han pagado?
- Listado de Morosidad
- Historial de Pagos - Historial de Pago de las viviendas

- Listado de Usuario - Lista de Usuarios

- Historial de Bitacora

*/

/* Listado de Vivienda - Vivienda + Quien es su propietario?

SELECT DISTINCT ON (V.id) 
    V.calle AS "Calle",
    V.numero_vivienda AS "Número de Vivienda", 
    CASE 
        WHEN R.id IS NULL THEN 'No asignado' 
        ELSE TRIM(CONCAT(R.nombre, ' ', R.apellido)) 
    END AS "Propietario",
    COALESCE(CAST(R.cedula AS VARCHAR), 'No asignada') AS "Cédula", 
    COALESCE(R.telefono, 'No asignado') AS "Teléfono"
FROM viviendas AS V
LEFT JOIN representantes AS R ON V.id = R.id_vivienda
WHERE V.activo = True
ORDER BY V.id, R.id ASC;
*/

/* Listado de Representante - Vivienda + Quienes son su representantes ?

SELECT V.calle AS "Calle",
		V.numero_vivienda AS "Número de Vivienda", 
		CONCAT(R.nombre,' ',R.apellido) AS "Representante", 
		R.cedula AS "Cédula", 
		R.telefono AS "Teléfono"
FROM viviendas AS V
LEFT JOIN representantes AS R ON  V.id = R.id_vivienda
WHERE V.activo = True AND R.activo = True
*/

/* Listado de Carnets - Vivienda + Cuales Carnets tiene asociados ?

SELECT C.codigo AS "Código",
		V.numero_vivienda AS "Número de Vivienda",
		V.calle AS "Calle"
FROM viviendas AS V
LEFT JOIN carnets AS C ON  V.id = C.id_vivienda
WHERE V.activo = True AND C.activo = True

*/

/* Lista de accesos por Carnets

SELECT A.fecha_hora AS "Fecha-Hora", 
		A.tipo AS "Tipo", 
		A.estado AS "Estado", 
		C.codigo AS "Carnet", 
		V.calle AS "Calle", 
		V.numero_vivienda AS "Número Vivienda"
FROM accesos AS A
JOIN carnets AS C ON C.id = A.id_carnet
JOIN viviendas AS V ON V.id = C.id_vivienda
WHERE A.nombre_visita IS NULL

*/

/* Lista de accesos por Visita

SELECT A.fecha_hora AS "Fecha-Hora", 
		A.tipo AS "Tipo", 
		A.estado AS "Estado", 
		A.nombre_visita AS "Nombre de Visita"
FROM accesos AS A
WHERE A.id_carnet IS NULL

*/

/* Listado Cuotas - Listado de Cuotas + Cuanto han Ganado? + Cuantos Han pagado?

SELECT C.descripcion AS "Descripción",
		C.monto AS "Monto",
		C.fecha_emision AS "Fecha Emisión",
		C.fecha_limite AS "Fecha Limite",
		COUNT(PR.id) AS "Pagaron",
		C.monto*COUNT(PR.id) AS "Acumulado"
FROM cuotas AS C
JOIN pagos_realizados AS PR ON C.id = PR.id_cuota

WHERE C.borrada = False AND C.activo = True
GROUP BY C.id

*/

/* Listado de Morosidad

SELECT V.calle "Calle",
		V.numero_vivienda "Número de Vivienda",
		solvencia(V.id) AS "Estado",
		deuda(V.id) AS "Deuda"
FROM viviendas AS  V
WHERE V.activo = True

*/

/* Historial de Pagos - Historial de Pago de las viviendas

SELECT V.calle AS "Calle",
		V.numero_vivienda AS "Número de Vivienda",
		C.descripcion AS "Descripción",
		C.monto AS "Monto",
		PR.tipo_pago AS "Tipo de Pago", 
		PR.referencia AS "Referencia"
FROM pagos_realizados AS PR
JOIN viviendas AS V ON V.id = PR.id_vivienda
JOIN cuotas AS C ON V.id = PR.id_cuota

*/

/* Listado de Usuario - Lista de Usuarios

SELECT U.rol AS "Rol", 
		CONCAT(U.nombre,' ',U.apellido) AS "Nombre Completo",
		U.cedula AS "Cédula", 
		U.telefono AS "Teléfono"
FROM usuarios AS U
WHERE activo = true

*/

/* Historial de Bitacora

SELECT B.usuario AS "Usuario", 
		B.accion AS "Acción", 
		B.tabla_modificada AS "Módulo", 
		B.fecha_modificacion AS "Fecha"
FROM bitacoras AS B

*/