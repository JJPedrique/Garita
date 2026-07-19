package Frontend.Mantenimiento;
import javax.swing.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;


public class SubMenuGestionDeRespaldos extends JPanel {

    public SubMenuGestionDeRespaldos(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc= new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx =0;
        gbc.insets = new Insets(10,10,10,10);

        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        
        JLabel TextRespaldo = ThemeManager.Label("GENERAR RESPALDO DE LA BASE DE DATOS");
        TextRespaldo.setFont(ThemeManager.TEXT_SUBTITLE);
        TextRespaldo.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(TextRespaldo,gbc);        

        gbc.gridy=1;
        JButton BtnRespladar = ThemeManager.Button("Generar Respaldo de la Base de Datos");
        BtnRespladar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {Respaldar();}
        });
        this.add(BtnRespladar,gbc);

        gbc.gridy = 2; gbc.weightx =1; gbc.gridwidth =2;
        JSeparator Line1 =  new JSeparator();
        Line1.setOrientation(SwingConstants.HORIZONTAL);
        this.add(Line1,gbc);

        gbc.gridy=3;gbc.weightx =0;  gbc.gridwidth =1;
        JLabel TextRespaldar = ThemeManager.Label("RESTAURAR LA BASE DE DATOS");
        TextRespaldar.setFont(ThemeManager.TEXT_SUBTITLE);
        TextRespaldar.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(TextRespaldar,gbc); 

        gbc.gridy=4; 
        JButton BtnRestaurar = ThemeManager.Button("Restaurar la Base de Datos");
        BtnRestaurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {Restaurar();}
        });
        this.add(BtnRestaurar,gbc);

        gbc.gridy=5; gbc.weighty=1;gbc.fill=GridBagConstraints.BOTH;
        this.add(new JLabel(),gbc);
    }

    void Respaldar(){
       try {ConexionPostgres.backupDatabase();
        ThemeManager.MostrarMensajeExito(this,"Se ha generado un respaldo en la carpeta descargas");
        } catch (Exception e) {ThemeManager.MostrarMensajeError(this,"Ocurrio un Problema, no se logro Respaldar.");}
    }
   
    void Restaurar(){
       try {
            ConexionPostgres.restoreDatabase();
            ThemeManager.MostrarMensajeExito(this,"Se han restaurado los datos correctamente, reinicie el programa para ver los cambios.");
            System.exit(0);
        } catch (Exception e) {ThemeManager.MostrarMensajeError(this,"Ocurrio un Problema, no se logro Restaurar.");}
    }
}