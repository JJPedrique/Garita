package Frontend.Mantenimiento.Usuarios;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

import org.openpdf.text.alignment.HorizontalAlignment;

import Backend.ConexionPostgres;
import Backend.ThemeManager;



public class FrameBorrarUsuario {
    
    int id;
    String INIT_SQL = "SELECT nombre,apellido,cedula FROM usuarios WHERE id = ?;";
    String SQL = "UPDATE usuarios SET activo = ? WHERE id = ?;";
    String Nombre,Apellido,Cedula;

    public FrameBorrarUsuario(int newId){
        id = newId;
        Init();
        boolean confirmar = ThemeManager.MostrarConfirmacion(
            new JPanel(),
            "Sistema Garita - Eliminar Usuario",
            "¿Desea eliminar el usuario " + Nombre + " " + Apellido + " - " + Cedula + "? Se eliminará permanentemente.",
            ThemeManager.COLOR_ERROR,
            "Eliminar",
            "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            ConexionPostgres.comandoDML(SQL,new Object[]{false,id});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ThemeManager.MostrarMensajeExito(new JPanel(),"Usuario eliminado exitosamente.");
    }

    void Init(){
        
        try {
        ResultSet RS_DATA = ConexionPostgres.consultar(INIT_SQL,new Object[]{id});
        while (RS_DATA.next()) {
            Nombre = (RS_DATA.getString("nombre"));
            Apellido = (RS_DATA.getString("apellido"));
            Cedula = (RS_DATA.getString("cedula"));
        }
    
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}