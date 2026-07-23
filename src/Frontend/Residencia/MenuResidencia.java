package Frontend.Residencia;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.ThemeManager;

import java.awt.*;

public class MenuResidencia extends JPanel {
    public MenuResidencia(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        UIManager.put("TabbedPane.selected", ThemeManager.COLOR_BACKGROUND_LIGHT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Viviendas",new MenuVivienda());
        TabMenu.add("Representantes",new MenuRepresentante());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
    }
}