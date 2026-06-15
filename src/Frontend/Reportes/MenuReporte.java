package Frontend.Reportes;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

import Backend.ThemeManager;

public class MenuReporte extends JPanel{
    public MenuReporte() throws SQLException{
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();

        TabMenu.addTab("Basico",new MenuFinalReporte());
        TabMenu.addTab("General",new ReporteGeneral());

        this.add(new JLabel("Módulo - Reportes"),BorderLayout.NORTH);
        this.add(TabMenu,BorderLayout.CENTER);

        TabMenu.setFont(ThemeManager.TEXT_NORMAL);
    }
}
