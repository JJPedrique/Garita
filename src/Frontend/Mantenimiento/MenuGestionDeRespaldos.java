package Frontend.Mantenimiento;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

public class MenuGestionDeRespaldos extends JPanel {
    JLabel Last = new JLabel("Ultima Modificación: XXXX-XX-XX XX:XXxx");

    public MenuGestionDeRespaldos(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc= new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx =0;
        gbc.insets = new Insets(10,10,10,10);

        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        
        JButton BtnRespladar = BTN("Generar Respaldo de la Base de Datos");
        this.add(BtnRespladar,gbc);

        gbc.gridy = 1; gbc.weightx =1; gbc.gridwidth =2;
        JSeparator Line1 =  new JSeparator();
        Line1.setOrientation(SwingConstants.HORIZONTAL);
        this.add(Line1,gbc);

        gbc.gridy =2; gbc.weightx =0; gbc.gridwidth =1;
        JButton BtnRestaurar = BTN("Restaurar de la Base de Datos");
        this.add(BtnRestaurar,gbc);

        gbc.gridy = 3;gbc.weightx =1; gbc.gridwidth =2;
        JSeparator Line2 =  new JSeparator();
        this.add(Line2,gbc);

        gbc.gridy = 4;gbc.weightx =1; gbc.gridwidth =2;
        Last.setFont(ThemeManager.TEXT_NORMAL);
        Last.setForeground(ThemeManager.COLOR_TEXT);
        this.add(Last,gbc);
    }

    JButton BTN(String texto){
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(175, 40));
        btn.setForeground(ThemeManager.COLOR_TEXT);
        btn.setBackground(ThemeManager.COLOR_PRIMARY);
        btn.setFont(ThemeManager.TEXT_SUBTITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });

        return btn;
    }


    public boolean generarRespaldo(String pgDumpPath, String host, String port, 
                                          String user, String password, String dbName, 
                                          String outputPath) throws IOException, InterruptedException {
        
        // Construimos el comando pg_dump
        // --clean: Incluye comandos para borrar (DROP) los objetos antes de crearlos
        // --file: Indica la ruta del archivo de salida
        ProcessBuilder pb = new ProcessBuilder(
            pgDumpPath,
            "--host=" + host,
            "--port=" + port,
            "--username=" + user,
            "--clean",
            "--file=" + outputPath,
            dbName
        );

        /*
            ProcessBuilder pb = new ProcessBuilder(
                "psql", 
                "--host=" + host, 
                "--port=" + port, 
                "--username=" + user, 
                "--dbname=" + dbName, 
                "--file=" + outputPath
            );
        */

        // Pasamos la contraseña de forma segura mediante variables de entorno
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", password);

        // Redirigimos los errores para poder verlos en la consola de Java si algo falla
        pb.redirectErrorStream(true);

        // Iniciamos el proceso
        Process proceso = pb.start();

        // Esperamos a que termine y obtenemos el código de salida (0 significa éxito)
        int codigoSalida = proceso.waitFor();
        
        return codigoSalida == 0;
    }


}