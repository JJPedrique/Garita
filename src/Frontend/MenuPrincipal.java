package Frontend;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import Frontend.Bitacora.MenuBitacora;
import Frontend.ControlDeAcceso.MenuControlDeAcceso;
import Frontend.Cuotas.MenuCuotas;
import Frontend.Mantenimiento.MenuMantenimiento;
import Frontend.Reportes.MenuReporte;
import Frontend.Residencia.MenuResidencia;

public class MenuPrincipal extends JPanel {

    CardLayout MainLayout = new CardLayout();
    JPanel Main = new JPanel(MainLayout);

    public MenuPrincipal() throws Exception{
        setLayout(new BorderLayout());
        this.add(SideBar(),BorderLayout.WEST);

        Main.add("Residencia", new MenuResidencia());
        Main.add("ControlDeAcceso", new MenuControlDeAcceso());
        Main.add("Cuotas", new MenuCuotas());
        Main.add("Reportes", new MenuReporte());
        Main.add("Bitacora", new MenuBitacora());
        Main.add("Mantenimiento", new MenuMantenimiento());
        
        this.add(Main,BorderLayout.CENTER);
    }

    JPanel SideBar(){
        JPanel newPanel = new JPanel();
        newPanel.setBackground(ThemeManager.COLOR_PRIMARY); // Gris oscuro
        newPanel.setPreferredSize(new Dimension(175, getHeight()));
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));      
        
        JLabel lblTitulo = new JLabel("GARITA");
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setFont(ThemeManager.TEXT_TITLE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        newPanel.add(lblTitulo);

        newPanel.add(SidebarButton("Residencia", "Residencia"));
        newPanel.add(SidebarButton("Control de Acceso", "ControlDeAcceso"));
        newPanel.add(SidebarButton("Cuotas", "Cuotas"));   
        newPanel.add(SidebarButton("Reportes", "Reportes"));
        newPanel.add(SidebarButton("Bitacora", "Bitacora"));
        newPanel.add(SidebarButton("Mantenimiento", "Mantenimiento"));
        newPanel.add(HelpButton());
        newPanel.add(ExitButton());
        
        return newPanel;
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

    JButton SidebarButton(String texto, final String nombreVista) {
        JButton btn = BTN(texto);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainLayout.show(Main, nombreVista);
            }
        });

        return btn;
    }

   JButton HelpButton() {
        JButton btn = BTN("Ayuda");

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File archivoHtml = new File("src/Frontend/Ayuda/help.html");
                    if (archivoHtml.exists()) {
                        Desktop.getDesktop().browse(archivoHtml.toURI());
                    } else {
                        System.out.println("El archivo no existe.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return btn;
    }

   JButton ExitButton() {
        JButton btn = BTN("Salir");

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return btn;
    }

}
