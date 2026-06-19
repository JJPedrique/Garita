--LISTA DE MOROSOS
SELECT viviendas.categoria,viviendas.numero_vivienda,
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

SELECT viviendas.categoria AS Calle,
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
SELECT viviendas.categoria AS Calle, 
		viviendas.numero_vivienda AS Vivienda , 
		COUNT(representantes.id) AS  Representantes, 
		COUNT(carnets.id) AS Carntes
FROM viviendas
JOIN representantes ON viviendas.id = representantes.id_vivienda
JOIN carnets ON viviendas.id = carnets.id_vivienda
GROUP BY viviendas.categoria, viviendas.numero_vivienda

--REPRESENTANTES
SELECT viviendas.categoria AS Calle, 
		viviendas.numero_vivienda AS Vivienda,
		representantes.nombre AS Nombre,
		representantes.apellido AS Apellido,
		representantes.cedula AS Cedula,
		representantes.telefono AS Telefono  
FROM representantes
JOIN viviendas ON viviendas.id = representantes.id_vivienda 

--CARNETS
SELECT viviendas.categoria AS Calle,
 		viviendas.numero_vivienda AS Vivienda, 
 		carnets.codigo AS Codigo
FROM carnets
JOIN viviendas ON viviendas.id = carnets.id_vivienda 

--REGISTRO DE ACCESOS (MUCHOS NULL)
SELECT accesos.fecha_hora AS "Fecha de Acceso",
 		accesos.tipo AS Tipo,
		accesos.estado AS Estado, 
 		viviendas.categoria AS Calle, 
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
SELECT viviendas.categoria AS Calle,
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
