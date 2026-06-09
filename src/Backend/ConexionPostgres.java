package Backend;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

import javax.swing.JOptionPane;

public class ConexionPostgres {
    
    Connection conexion = null;

    // Configuración de la base de datos
    // El puerto por defecto de PostgreSQL es 5432      
    public static final String HOST = "localhost";
    public static final String PORT = "5432"; 
    public static final String USER = "postgres";
    public static final String DB_NAME = "Garita";
    private static final String PASSWORD = "1234";

    private static final String URL = "jdbc:postgresql://"+HOST+":"+PORT+"/"+DB_NAME+""; 
    static final String userHome = System.getProperty("user.home");
    static final String DEST_PATH = "C:/respaldos/Garita.backup";


    public Connection conectar() {
        this.conexion = null;
        
        try {
            // Intentar establecer la conexión
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
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

   public static boolean backupDatabase() {
        try {
            // Asegurar que el directorio de destino exista
            File outputFile = new File(DEST_PATH);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }       
            // pg_dump -F c (formato personalizado, ideal para pg_restore)
            ProcessBuilder pb = new ProcessBuilder(
                "C:\\Program Files\\PostgreSQL\\16\\bin\\pg_dump.exe",
                "-h", HOST,
                "-p", PORT,
                "-U", USER,
                "-F", "c", 
                "-b", // Incluir blobs grandes
                "-v", // Modo detallado (verbose)
                "-f",  DEST_PATH,
                DB_NAME
            );

            // Inyectar la contraseña de forma segura en las variables de entorno del proceso
            Map<String, String> env = pb.environment();
            env.put("PGPASSWORD", PASSWORD);

            // Redirigir errores al flujo estándar para poder leerlos si algo falla
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Respaldo creado exitosamente en: " + DEST_PATH);
                return true;
            } else {
                System.err.println("Error al crear el respaldo. Código de salida: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static boolean restoreDatabase() {
        try {
            // pg_restore -c (limpia/elimina objetos antes de recrearlos) -d (base de datos destino)
            ProcessBuilder pb = new ProcessBuilder(
                "pg_restore",
                "-h", HOST,
                "-p", PORT,
                "-U", USER,
                "-d", DB_NAME,
                "-c", // Limpia la base de datos antes de restaurar (opcional)
                "-v", // Modo detallado
                DEST_PATH
            );

            // Inyectar la contraseña
            Map<String, String> env = pb.environment();
            env.put("PGPASSWORD", PASSWORD);

            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Base de datos restaurada exitosamente desde: " + DEST_PATH);
                return true;
            } else {
                System.err.println("Error al restaurar la base de datos. Código de salida: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }

}