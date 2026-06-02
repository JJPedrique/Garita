package Frontend.ControlDeAcceso;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;

public class MenuControlDeAcceso extends JPanel {
    public MenuControlDeAcceso(){
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Carnets",new MenuCarnets());
        TabMenu.add("Registro de Acceso",new MenuRegistroDeAcceso());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
    }
}