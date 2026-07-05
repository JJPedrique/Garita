package Frontend.Mantenimiento;
import javax.swing.*;

import Backend.ThemeManager;
import Frontend.Mantenimiento.Usuarios.SubMenuUsuarios;

import java.awt.*;

public class MenuMantenimiento extends JPanel {
    public MenuMantenimiento(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Gestion de Respaldos",new SubMenuGestionDeRespaldos());
        TabMenu.add("Usuarios",new SubMenuUsuarios());       
        this.add(TabMenu, BorderLayout.CENTER);

        UIManager.put("TabbedPane.tabAreaBackground", ThemeManager.COLOR_BACKGROUND_DARK);
        UIManager.put("TabbedPane.background", ThemeManager.COLOR_BACKGROUND_DARK);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
    }
}