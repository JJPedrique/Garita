package BDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionPostgres {
    
    Connection conexion = null;
    
    // Configuración de la base de datos
    // El puerto por defecto de PostgreSQL es 5432                
    private static final String URL = "jdbc:postgresql://localhost:5433/SimCityBD"; 
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "root1234";

    public Connection conectar() {
        this.conexion = null;
        
        try {
            // Intentar establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión establecida con éxito a PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
        
        return conexion;
    }   
    
    // para el INSERT | UPDATE | DELETE
    public void comandoDML(String QUERY, Object VALUES[]) throws SQLException{
        
        if (this.conexion == null || this.conexion.isClosed()) {
            conectar();
        }
        
        try (PreparedStatement PS = conexion.prepareStatement(QUERY)) {
            for(int i=0; i<VALUES.length; i++){
                if(VALUES[i] instanceof String){
                    PS.setString(i+1, (String) VALUES[i]);
                }
                if(VALUES[i] instanceof Boolean){
                    PS.setBoolean(i+1, (boolean) VALUES[i]);
                }
                if(VALUES[i] instanceof Double){
                    PS.setDouble(i+1, (Double) VALUES[i]);
                }
                if(VALUES[i] instanceof Number){
                    PS.setInt(i+1, (int) VALUES[i]);
                }
            }
            
            // SOLO PARA VERIFICAR QUERY
//            String queryEjecutable = QUERY;
//            for (Object p : VALUES) {
//                String valor = (p instanceof String) ? "'" + p + "'" : String.valueOf(p);
//                queryEjecutable = queryEjecutable.replaceFirst("\\?", valor);
//            }
//            JOptionPane.showMessageDialog(null,queryEjecutable);
    
            int filasAfectadas = PS.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Comando DML Ejecutado Correctamente.");
            }
            
        } catch(SQLException SQLE) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + SQLE.getMessage());
        }
    }
    
    // Para SELECT
    public ResultSet consultar(String QUERY, Object[] PARAMETROS) throws SQLException {
        
        if (this.conexion == null || this.conexion.isClosed()) {
            conectar();
        }
        
        try {
            PreparedStatement PS = this.conexion.prepareStatement(QUERY);
            if (PARAMETROS != null) {
                for (int i=0; i<PARAMETROS.length; i++) {
                    if (PARAMETROS[i] instanceof String) {
                        PS.setString(i+1,PARAMETROS[i].toString());
                    } else {
                        PS.setObject(i+1, PARAMETROS[i]);
                    }
                }
            }

            return PS.executeQuery();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error SQL: " + e.getMessage() + "\nQuery: " + QUERY);
            return null;
        }
    }
}