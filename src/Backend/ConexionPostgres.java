package Backend;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConexionPostgres {
    
    public static Connection conexion = null;

    // Configuración de la base de datos
    // El puerto por defecto de PostgreSQL es 5432    
    //#region POSTGRE 15  
    //#endregion
    static final String pgDumpPath = "C:\\Program Files\\PostgreSQL\\15\\bin\\pg_dump.exe";
    static final String pgRestorePath = "C:\\Program Files\\PostgreSQL\\15\\bin\\pg_restore.exe";  
    static final String psqlPath = "C:\\Program Files\\PostgreSQL\\15\\bin\\psql.exe";
    public static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static final String DEFAULT = "jdbc:postgresql://localhost:5432/postgres"; 
    private static final String URL = "jdbc:postgresql://localhost:5432/garita"; 
    static final String userHome = System.getProperty("user.home");
    static final String DEST_PATH = "C:/respaldos/Garita.backup";


    public static Connection conectar() {
        ConexionPostgres.conexion = null;
        
        try {
            // Intentar establecer la conexión
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con éxito a PostgreSQL.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al conectar: Driver JDBC de PostgreSQL no encontrado en el classpath.");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
        
        return conexion;
    }   
    
    // para el INSERT | UPDATE | DELETE
    public static void comandoDML(String QUERY, Object VALUES[]) throws SQLException{
        
        if (ConexionPostgres.conexion == null || ConexionPostgres.conexion.isClosed()) {
            conectar();
        }

        if (ConexionPostgres.conexion == null) {
            throw new SQLException("No se pudo establecer conexión a la base de datos. Verifique el driver JDBC, URL, usuario y contraseña.");
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
            // String queryEjecutable = QUERY;
            // for (Object p : VALUES) {
            //     String valor = (p instanceof String) ? "'" + p + "'" : String.valueOf(p);
            //     queryEjecutable = queryEjecutable.replaceFirst("\\?", valor);
            // }
            // System.out.println(queryEjecutable);
    
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
    public static ResultSet consultar(String QUERY, Object[] PARAMETROS) throws SQLException {
        
        if (ConexionPostgres.conexion == null || ConexionPostgres.conexion.isClosed()) {
            conectar();
        }

        if (ConexionPostgres.conexion == null) {
            throw new SQLException("No se pudo establecer conexión a la base de datos. Verifique el driver JDBC, URL, usuario y contraseña.");
        }
        
        try {
            PreparedStatement PS = ConexionPostgres.conexion.prepareStatement(QUERY);
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

    public static void crearBaseDeDatos(String nombreDb) {
        try (Connection conn = DriverManager.getConnection(DEFAULT, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Prevenir inyección SQL básica si el nombre viene de input externo
            if (!nombreDb.matches("^[a-zA-Z0-9_]+$")) {
                throw new IllegalArgumentException("Nombre de base de datos inválido.");
            }

            String sql = "CREATE DATABASE " + nombreDb;
            stmt.execute(sql);
            System.out.println("Base de datos '" + nombreDb + "' creada exitosamente.");

        } catch (Exception e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
        }
    }

    public static void InicializarBaseDeDatos() throws Exception{
        // PASO 2: Conectar a 'garita' y ejecutar la estructura
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Ejecutando script de estructura y datos por defecto...");
            String sqlEstructura = Files.readString(Path.of("src\\Backend\\BDD\\InitDatabase.sql"));
            
            stmt.executeUpdate(sqlEstructura); 
            System.out.println("Estructura, funciones, triggers y usuarios por defecto generados correctamente.");

        } catch (SQLException e) {
            System.err.println("Error ejecutando el script de tablas: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo 2: " + e.getMessage());
        }
    }


    public static void backupDatabase() throws Exception {
        try {
            
            String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";
            File archivo = new File(rutaDescargas, "GaritaRespaldo.backup");
    
            ProcessBuilder pb = new ProcessBuilder(
                pgDumpPath, 
                "-h", "localhost", 
                "-U", "postgres", 
                "-F", "c",                 
                "-f", archivo.getAbsolutePath(), 
                "garita"
            );

            Map<String, String> entorno = pb.environment();
            entorno.put("PGPASSWORD", PASSWORD);
            pb.inheritIO(); 
            Process proceso = pb.start();
            int codigoSalida = proceso.waitFor();

            if (codigoSalida == 0) {
                System.out.println("¡Respaldo creado con éxito!");
            } else {
                System.out.println("Hubo un error. Código de salida de Postgres: " + archivo.getAbsolutePath());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error al ejecutar el proceso: " + e.getMessage());
            throw new Exception();
        }
    }

    public static void restoreDatabase() throws Exception {
        try {
            try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {System.out.println("No se pudo cargar el aspecto nativo de Windows.");}

            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Seleccione el archivo de respaldo (.backup)");
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Respaldos Postgres (*.backup)", "backup");
            selector.setFileFilter(filtro);
            String rutaUsuario = System.getProperty("user.home");
            selector.setCurrentDirectory(new File(rutaUsuario, "Downloads"));

            int resultado = selector.showOpenDialog(null);
            if (resultado != JFileChooser.APPROVE_OPTION) {System.out.println("Restauración cancelada por el usuario.");return;}
            File archivoSeleccionado = selector.getSelectedFile();
           
            Statement stmt = conexion.createStatement();
            stmt.execute("DROP SCHEMA public CASCADE; CREATE SCHEMA public;");
                
            ProcessBuilder pb = new ProcessBuilder(
                pgRestorePath, 
                "-h", "localhost", 
                "-U", "postgres", 
                "-d", "garita", 
                archivoSeleccionado.getAbsolutePath()      
            );

            Map<String, String> entorno = pb.environment();
            entorno.put("PGPASSWORD", PASSWORD);
            pb.inheritIO(); 
            Process proceso = pb.start();
            int codigoSalida = proceso.waitFor();

            if (codigoSalida == 0) {
                System.out.println("¡Base de datos restaurada con éxito desde el archivo seleccionado!");
            } else {
                System.out.println("Hubo un error al restaurar. Código de salida: " + codigoSalida);
                
            }
        

        } catch (IOException | InterruptedException e) {
            throw new Exception();
        }
    }
}