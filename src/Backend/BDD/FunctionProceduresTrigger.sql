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

CREATE OR REPLACE FUNCTION solvencia(integer)
RETURNS DECIMAL(6,2)
AS $$
DECLARE 
	PAGOS_REALIZADOS DECIMAL(6,2);
	CUOTAS DECIMAL(6,2);
BEGIN
    SELECT Count(pagos_realizados.id) INTO PAGOS_REALIZADOS
    from pagos_realizados
    where id_vivienda = $1;

    SELECT Count(cuotas.id) INTO CUOTAS
    FROM cuotas; 

    IF PAGOS_REALIZADOS = CUOTAS THEN
        RETURN "Solvente";
    ELSE 
        RETURN "Moroso";
    END IF;
END;
$$ 
LANGUAGE plpgsql;