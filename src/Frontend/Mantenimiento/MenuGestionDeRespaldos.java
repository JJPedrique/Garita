package Frontend.Mantenimiento;
import javax.swing.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;


public class MenuGestionDeRespaldos extends JPanel {
    ConexionPostgres DB = new ConexionPostgres();
    JLabel Last = new JLabel("Ultima Modificación: XXXX-XX-XX XX:XXxx");

    public MenuGestionDeRespaldos(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc= new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx =0;
        gbc.insets = new Insets(10,10,10,10);

        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        
        JButton BtnRespladar = BTN("Generar Respaldo de la Base de Datos");
        BtnRespladar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(ConexionPostgres.backupDatabase());
            }
        });
        this.add(BtnRespladar,gbc);

        gbc.gridy = 1; gbc.weightx =1; gbc.gridwidth =2;
        JSeparator Line1 =  new JSeparator();
        Line1.setOrientation(SwingConstants.HORIZONTAL);
        this.add(Line1,gbc);

        gbc.gridy =2; gbc.weightx =0; gbc.gridwidth =1;
        JButton BtnRestaurar = BTN("Restaurar de la Base de Datos");
        BtnRestaurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConexionPostgres.restoreDatabase();
            }
        });
        this.add(BtnRestaurar,gbc);

        gbc.gridy = 3;gbc.weightx =1; gbc.gridwidth =2;
        JSeparator Line2 =  new JSeparator();
        this.add(Line2,gbc);

        gbc.gridy = 4;gbc.weightx =1; gbc.gridwidth =2;
        Last.setFont(ThemeManager.TEXT_NORMAL);
        Last.setForeground(ThemeManager.COLOR_TEXT);
        this.add(Last,gbc);
    }

    JButton BTN(String texto){
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(175, 40));
        btn.setForeground(ThemeManager.COLOR_TEXT);
        btn.setBackground(ThemeManager.COLOR_PRIMARY);
        btn.setFont(ThemeManager.TEXT_SUBTITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });

        return btn;
    }


   

}