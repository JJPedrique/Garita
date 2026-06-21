package Backend;
import java.io.*;
import java.sql.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConexionPostgres {
    
    Connection conexion = null;

    // Configuración de la base de datos
    // El puerto por defecto de PostgreSQL es 5432      
    public static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static final String URL = "jdbc:postgresql://localhost:5432/Garita"; 
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
        if (VALUES[i] == null) {
                PS.setNull(i + 1, java.sql.Types.NULL);
            } else if (VALUES[i] instanceof String) {
                PS.setString(i + 1, (String) VALUES[i]);
            } else if (VALUES[i] instanceof Boolean) {
                PS.setBoolean(i + 1, (boolean) VALUES[i]);
            } else if (VALUES[i] instanceof Double) { 
            
                PS.setDouble(i + 1, (Double) VALUES[i]);
            } else if (VALUES[i] instanceof java.sql.Timestamp) { 
                PS.setTimestamp(i + 1, (java.sql.Timestamp) VALUES[i]);
            } else if (VALUES[i] instanceof Number) { 
                PS.setInt(i + 1, ((Number) VALUES[i]).intValue());
            } else {
                PS.setObject(i + 1, VALUES[i]); 
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
            throw SQLE;
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

    public static void backupDatabase() {
        try {
            // 1. Conseguimos la ruta de la carpeta de Descargas de forma nativa
            String rutaUsuario = System.getProperty("user.home");
            File carpetaDescargas = new File(rutaUsuario, "Downloads");

            // 2. Definimos el comando pg_dump
            // CORRECCIÓN: Se cambió "-F", "p" por "-F", "c" (Formato Custom/Binario)
            ProcessBuilder pb = new ProcessBuilder(
                "pg_dump", 
                "-h", "localhost", 
                "-U", "postgres", 
                "-F", "c",                 // <--- CAMBIADO A FORMATO BINARIO 'c'
                "-f", "GaritaRespaldo.backup", 
                "Garita"
            );

            // Le dice a Java dónde guardar el archivo
            pb.directory(carpetaDescargas);

            // 3. Inyectamos la variable de entorno PGPASSWORD directamente al proceso
            Map<String, String> entorno = pb.environment();
            entorno.put("PGPASSWORD", PASSWORD);

            // Esto hace que los errores de Postgres se muestren en la consola de Java
            pb.inheritIO(); 

            // 4. Arrancamos el proceso
            System.out.println("Iniciando respaldo en: " + carpetaDescargas.getAbsolutePath());
            Process proceso = pb.start();
            
            // Esperamos a que termine de ejecutarse
            int codigoSalida = proceso.waitFor();

            if (codigoSalida == 0) {
                System.out.println("¡Respaldo creado con éxito!");
            } else {
                System.out.println("Hubo un error. Código de salida de Postgres: " + codigoSalida);
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error al ejecutar el proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void restoreDatabase() {
        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("No se pudo cargar el aspecto nativo de Windows.");
            }

            // 1. Configurar y abrir el explorador de archivos (JFileChooser)
            JFileChooser selector = new JFileChooser();
            // CORRECCIÓN: Modificado el título del diálogo
            selector.setDialogTitle("Seleccione el archivo de respaldo (.backup)");
            
            // CORRECCIÓN: Ahora el filtro busca archivos .backup reales
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Respaldos Postgres (*.backup)", "backup");
            selector.setFileFilter(filtro);
            
            // Abrir por defecto en la carpeta de Descargas del usuario
            String rutaUsuario = System.getProperty("user.home");
            selector.setCurrentDirectory(new File(rutaUsuario, "Downloads"));

            // Mostrar el explorador
            int resultado = selector.showOpenDialog(null);

            // 2. Si el usuario selecciona un archivo y presiona "Abrir"
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = selector.getSelectedFile();
                
                // Obtenemos la ruta absoluta del archivo y el directorio donde se encuentra
                String rutaArchivoBackup = archivoSeleccionado.getAbsolutePath();
                File directorioArchivo = archivoSeleccionado.getParentFile();

                // 3. Configurar el ProcessBuilder con el archivo dinámico
                // Aquí el uso de pg_restore y --clean es 100% correcto porque el archivo ahora sí es binario
                ProcessBuilder pb = new ProcessBuilder(
                    "pg_restore", 
                    "-h", "localhost", 
                    "-U", "postgres", 
                    "--clean",             
                    "-d", "Garita", 
                    rutaArchivoBackup      
                );

                // Establecemos el directorio de trabajo donde está el archivo
                pb.directory(directorioArchivo);

                // Configurar la contraseña de PostgreSQL
                Map<String, String> entorno = pb.environment();
                entorno.put("PGPASSWORD", PASSWORD);

                pb.inheritIO(); 

                System.out.println("Iniciando restauración desde el archivo: " + rutaArchivoBackup);
                Process proceso = pb.start();
                int codigoSalida = proceso.waitFor();

                if (codigoSalida == 0) {
                    System.out.println("¡Base de datos restaurada con éxito desde el archivo seleccionado!");
                } else {
                    System.out.println("Hubo un error al restaurar. Código de salida: " + codigoSalida);
                }
            } else {
                System.out.println("Restauración cancelada por el usuario.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}