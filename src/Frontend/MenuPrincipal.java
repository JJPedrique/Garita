package Frontend;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import Frontend.Bitacora.MenuBitacora;
import Frontend.ControlDeAcceso.MenuControlDeAcceso;
import Frontend.Cuotas.MenuCuotas;
import Frontend.InicioSesion.MenuInicioSesion;
import Frontend.Mantenimiento.MenuMantenimiento;
import Frontend.Reportes.MenuReporte;
import Frontend.Residencia.MenuResidencia;

public class MenuPrincipal extends JPanel {

    CardLayout MainLayout = new CardLayout();
    JPanel Main = new JPanel(MainLayout);

    public MenuPrincipal() throws Exception{
        setLayout(new BorderLayout());
        this.add(SideBar(),BorderLayout.WEST);

        if(MenuInicioSesion.rolUsuario.equals("Vigilancia")){
            Main.add("ControlDeAcceso", new MenuControlDeAcceso());
        }
        else if(MenuInicioSesion.rolUsuario.equals("Junta")){
            Main.add("Residencia", new MenuResidencia());
            Main.add("ControlDeAcceso", new MenuControlDeAcceso());
            Main.add("Cuotas", new MenuCuotas());
            Main.add("Reportes", new MenuReporte());
        }
        else{ // ROL ADMINISTRADOR
            Main.add("Residencia", new MenuResidencia());
            Main.add("ControlDeAcceso", new MenuControlDeAcceso());
            Main.add("Cuotas", new MenuCuotas());
            Main.add("Reportes", new MenuReporte());
            Main.add("Bitacora", new MenuBitacora());
            Main.add("Mantenimiento", new MenuMantenimiento());
        }
        
        this.add(Main,BorderLayout.CENTER);
    }

    JPanel SideBar(){
        JPanel newPanel = new JPanel();
        newPanel.setBackground(ThemeManager.COLOR_PRIMARY); // Gris oscuro
        newPanel.setPreferredSize(new Dimension(205, getHeight()));
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));      
        
        JLabel lblTitulo = new JLabel("GARITA");
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setFont(ThemeManager.TEXT_TITLE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        newPanel.add(lblTitulo);

        if(MenuInicioSesion.rolUsuario.equals("Vigilancia")){
            newPanel.add(SidebarButton("Control de Acceso", "ControlDeAcceso"));
        }
        else if(MenuInicioSesion.rolUsuario.equals("Junta")){
            newPanel.add(SidebarButton("Residencia", "Residencia"));
            newPanel.add(SidebarButton("Control de Acceso", "ControlDeAcceso"));
            newPanel.add(SidebarButton("Cuotas", "Cuotas"));   
            newPanel.add(SidebarButton("Reportes", "Reportes"));
        }
        else{ // ROL ADMINISTRADOR
            newPanel.add(SidebarButton("Residencia", "Residencia"));
            newPanel.add(SidebarButton("Control de Acceso", "ControlDeAcceso"));
            newPanel.add(SidebarButton("Cuotas", "Cuotas"));   
            newPanel.add(SidebarButton("Reportes", "Reportes"));
            newPanel.add(SidebarButton("Bitacora", "Bitacora"));
            newPanel.add(SidebarButton("Mantenimiento", "Mantenimiento"));
        }
    
        
        newPanel.add(HelpButton());
        newPanel.add(ExitButton());
        
        return newPanel;
    }

    JButton SidebarButton(String texto, final String nombreVista) {
        JButton btn = ThemeManager.SideBarButton(texto);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainLayout.show(Main, nombreVista);
            }
        });

        return btn;
    }

    JButton HelpButton() {
        JButton btn = ThemeManager.SideBarButton("Ayuda");

        btn.addActionListener(e -> {
            // 1. Nombre de la ruta del recurso dentro del JAR / classpath
            // Asegúrate de que el PDF se encuentre en src/img/Manual de Usuario.pdf
            String resourcePath = "/img/Manual de Usuario.pdf";

            try (InputStream pdfStream = getClass().getResourceAsStream(resourcePath)) {
                
                if (pdfStream == null) {
                    ThemeManager.MostrarMensajeError(this, "No se encontró el manual dentro del programa.");
                    return;
                }

                // 2. Crear un archivo temporal en el SO donde volcar el PDF
                File tempPdf = File.createTempFile("Manual_de_Usuario_", ".pdf");
                tempPdf.deleteOnExit(); // Se borrará automáticamente al cerrar la aplicación

                // 3. Copiar los bytes del recurso interno al archivo temporal
                Files.copy(pdfStream, tempPdf.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // 4. Abrir el archivo con el visor de PDF predeterminado del sistema
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(tempPdf);
                } else {
                    ThemeManager.MostrarMensajeError(this, "No se puede abrir archivos en esta plataforma.");
                }

            } catch (IOException ex) {
                ThemeManager.MostrarMensajeError(this, "Error al intentar abrir el manual.");
                ex.printStackTrace();
            }
        });

        return btn;
    }

   JButton ExitButton() {
        JButton btn = ThemeManager.SideBarButton("Salir");

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return btn;
    }

}
