package Frontend.Residencia;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.ThemeManager;

import java.awt.*;

public class MenuResidencia extends JPanel {
    public MenuResidencia(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        UIManager.put("TabbedPane.selected", ThemeManager.COLOR_BACKGROUND_LIGHT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaBackground", ThemeManager.COLOR_BACKGROUND_DARK);
        UIManager.put("TabbedPane.background", ThemeManager.COLOR_BACKGROUND_DARK);

        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Vivienda",new MenuVivienda());
        TabMenu.add("Representantes",new MenuRepresentante());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
    }
}