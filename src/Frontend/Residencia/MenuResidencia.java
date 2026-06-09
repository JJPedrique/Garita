package Frontend.Residencia;
import javax.swing.*;

import Backend.ThemeManager;

import java.awt.*;

public class MenuResidencia extends JPanel {
    public MenuResidencia(){
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Vivienda",new MenuVivienda());
        TabMenu.add("Representante",new MenuRepresentante());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
    }
}