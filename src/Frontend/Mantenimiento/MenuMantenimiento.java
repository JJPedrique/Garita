package Frontend.Mantenimiento;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;

public class MenuMantenimiento extends JPanel {
    public MenuMantenimiento(){
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Gestion de Respaldos",new MenuGestionDeRespaldos());
        TabMenu.add("Usuarios",new MenuUsuarios());       
        this.add(TabMenu, BorderLayout.CENTER);


        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
    }
}