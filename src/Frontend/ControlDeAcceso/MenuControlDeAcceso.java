package Frontend.ControlDeAcceso;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.ThemeManager;

import java.awt.*;

public class MenuControlDeAcceso extends JPanel {

    public int PADDING = 10;

    public MenuControlDeAcceso(){
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();
        TabMenu.add("Carnets",new MenuCarnets());
        TabMenu.add("Registro de Acceso",new MenuRegistroDeAcceso());       
        this.add(TabMenu, BorderLayout.CENTER);
        
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        UIManager.put("TabbedPane.selected", ThemeManager.COLOR_BACKGROUND_LIGHT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        this.setBorder(new EmptyBorder(PADDING,PADDING,PADDING,PADDING));
        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
        TabMenu.setForeground(ThemeManager.COLOR_TEXT);
        TabMenu.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
    }
}