package Frontend.ControlDeAcceso;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Backend.ThemeManager;
import Frontend.InicioSesion.MenuInicioSesion;

import java.awt.*;

public class MenuControlDeAcceso extends JPanel {

    public int PADDING = 10;

    public MenuControlDeAcceso(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        UIManager.put("TabbedPane.selected", ThemeManager.COLOR_BACKGROUND_LIGHT);
        UIManager.put("TabbedPane.background", ThemeManager.COLOR_BACKGROUND_DARK);
        UIManager.put("TabbedPane.foreground", ThemeManager.COLOR_TEXT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        if (MenuInicioSesion.rolUsuario.equals("Vigilancia")) {
            TabMenu.add("Registro de Acceso", new MenuRegistroDeAcceso());   
        } else {
            TabMenu.add("Carnets", new MenuCarnets());
            TabMenu.add("Registro de Acceso", new MenuRegistroDeAcceso());       
        }

        SwingUtilities.updateComponentTreeUI(TabMenu);
        this.add(TabMenu, BorderLayout.CENTER); 
    }
}