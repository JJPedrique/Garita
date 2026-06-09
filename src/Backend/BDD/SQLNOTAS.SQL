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