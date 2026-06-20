package Frontend.Reportes;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Backend.ConexionPostgres;
import Backend.DataInputs.*;

public class ReporteBasico extends JPanel {
   ConexionPostgres DB = new ConexionPostgres();
    
   public ReporteBasico(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(10,10,10,10);

        Input Date = new DateInput("Balance");
        this.add(Date,gbc);

        gbc.gridy=1;
        JButton BtnBalance = new JButton("Exportar en PDF Balance");
        this.add(BtnBalance,gbc);

        gbc.gridy=0;gbc.gridx=1;
        Input Str = new StringInput("Calle"); 
        this.add(Str,gbc);

        gbc.gridy=1;
        JButton BtnMoroso  = new JButton("Exportar en PDF Lista de Morosidad");
        this.add(BtnMoroso,gbc);
   }
}
