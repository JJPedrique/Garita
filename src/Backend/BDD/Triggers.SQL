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


CREATE TRIGGER trigger_auditoria_usuarios
AFTER INSERT OR UPDATE OR DELETE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_cuotas
AFTER INSERT OR UPDATE OR DELETE ON cuotas
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_viviendas
AFTER INSERT OR UPDATE OR DELETE ON viviendas
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_representantes
AFTER INSERT OR UPDATE OR DELETE ON representantes
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_carnets
AFTER INSERT OR UPDATE OR DELETE ON carnets
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_accesos
AFTER UPDATE OR DELETE ON accesos
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_pagosRealizados
AFTER UPDATE OR DELETE ON pagos_realizados
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();

CREATE TRIGGER trigger_auditoria_bitacoras
AFTER UPDATE OR DELETE ON bitacoras
FOR EACH ROW
EXECUTE FUNCTION registrar_en_bitacora();






             