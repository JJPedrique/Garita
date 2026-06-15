package Frontend.Residencia;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import Backend.ThemeManager;

import java.awt.*;

public class MenuResidencia extends JPanel {
    public MenuResidencia(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        UIManager.put("TabbedPane.selected", ThemeManager.COLOR_BACKGROUND_LIGHT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Vivienda",new MenuVivienda());
        TabMenu.add("Representante",new MenuRepresentante());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
    }
}