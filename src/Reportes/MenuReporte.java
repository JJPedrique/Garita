package Reportes;

import java.awt.*;
import java.sql.*;

import javax.swing.*;

public class MenuReporte extends JPanel{
    public MenuReporte() throws SQLException{
        this.setLayout(new BorderLayout());
        JTabbedPane TabMenu = new JTabbedPane();

        TabMenu.addTab("General",new ReporteGeneral());
        TabMenu.addTab("Avanzado",new ReporteAvanzado());

        this.add(new JLabel("Módulo - Reportes"),BorderLayout.NORTH);
        this.add(TabMenu,BorderLayout.CENTER);
    }
}
