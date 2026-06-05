import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class MenuInicioSesion extends JPanel{
    
    //region Componentes
    GridBagLayout GBL = new GridBagLayout();
    GridBagConstraints GBC = new GridBagConstraints();

    JPanel pHeader = new JPanel();
    JLabel lHeaderTitle = new JLabel("SISTEMA GARITA");


    JPanel pInput = new JPanel();
    JLabel lSubTitle = new JLabel("INICIAR SESIÓN");


    JPanel pButton = new JPanel();
    
    JLabel lInputUsuario = new JLabel("ICO1");
    JTextField tfInputUsuario = new JTextField("José Pérez");

    JLabel lInputClave = new JLabel("ICO2");
    JPasswordField pfClave = new JPasswordField("1234abcd");
    JToggleButton tbMostrarClave = new JToggleButton("OJO");

    JButton bOlvidaClave = new JButton("¿Olvidó su clave?");
    JButton bAcceder = new JButton("Acceder");
    JButton bSalir = new JButton("Salir");

    public void SetPosition(){

        //region Panel Principal
        setLayout(GBL);

        GBC.fill=GridBagConstraints.BOTH;
        GBC.anchor=GridBagConstraints.CENTER;

        GBC.weightx=1; 

        GBC.weighty=0; GBC.ipady=40;
        GBC.gridx=0; GBC.gridy=0; add(pHeader, GBC);

        GBC.weighty=1; GBC.ipady=0;
        GBC.gridx=0; GBC.gridy=1; add(pInput, GBC);

        GBC.weighty=0; GBC.ipady=50;
        GBC.gridx=0; GBC.gridy=2; add(pButton, GBC);

        //region Panel Header
        pHeader.setLayout(GBL);

        GBC.weighty=0; GBC.ipady=0;
        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER);
        GBC.gridx=0; GBC.gridy=0; pHeader.add(lHeaderTitle, GBC);

        //region Panel Input
        pInput.setLayout(GBL);

        GBC.gridwidth=2;

        GBC.weighty=0; GBC.ipady=0; 
        GBC.insets = new Insets(20,0,20,0);
        lSubTitle.setHorizontalAlignment(JLabel.CENTER);
        GBC.gridx=0; GBC.gridy=0; pInput.add(lSubTitle, GBC);

        GBC.gridwidth=1;
        
        GBC.insets = new Insets(6,48,8,8); GBC.ipady=0;
        GBC.gridx=0; GBC.gridy=1; GBC.weightx=0; pInput.add(lInputUsuario, GBC);
        GBC.insets = new Insets(6,8,8,48); GBC.ipady=10;
        GBC.gridx=1; GBC.gridy=1; GBC.weightx=1; pInput.add(tfInputUsuario, GBC);

        GBC.insets = new Insets(8,48,48,8); GBC.ipady=0;
        GBC.gridx=0; GBC.gridy=2; GBC.weightx=0; pInput.add(lInputClave, GBC);
        GBC.insets = new Insets(8,8,48,48); GBC.ipady=10;
        GBC.gridx=1; GBC.gridy=2; GBC.weightx=1; pInput.add(pfClave, GBC);

        //region Panel Button
        pButton.setLayout(GBL);

        GBC.gridwidth=1; GBC.weighty=1; GBC.ipady=0;
        GBC.insets = new Insets(16,16,4,16);
        GBC.gridx=0; GBC.gridy=0; pButton.add(bOlvidaClave, GBC);
        GBC.insets = new Insets(4,16,4,16);
        GBC.gridx=0; GBC.gridy=1; pButton.add(bAcceder, GBC);
        GBC.insets = new Insets(4,16,16,16);
        GBC.gridx=0; GBC.gridy=2; pButton.add(bSalir, GBC);
    }

    public void SetTheme(){
        setBackground(ThemeManager.COLOR_BACKGROUND);
        pHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pInput.setOpaque(false);
        pButton.setOpaque(false);

        lHeaderTitle.setFont(ThemeManager.TEXT_TITLE);
        lHeaderTitle.setForeground(ThemeManager.COLOR_TEXT);

        lSubTitle.setFont(ThemeManager.TEXT_SUBTITLE);
        lSubTitle.setForeground(ThemeManager.COLOR_TEXT);

        bOlvidaClave.setFont(ThemeManager.TEXT_NORMAL);
        bOlvidaClave.setOpaque(false);
        bOlvidaClave.setBackground(ThemeManager.COLOR_BACKGROUND);
        bOlvidaClave.setBorder(BorderFactory.createEmptyBorder());
        bOlvidaClave.setForeground(ThemeManager.COLOR_TEXT);

        bAcceder.setFont(ThemeManager.TEXT_SUBTITLE);
        bAcceder.setForeground(ThemeManager.COLOR_TEXT);
        bAcceder.setBackground(ThemeManager.COLOR_PRIMARY);
        bAcceder.setBorder(BorderFactory.createEmptyBorder());

        bSalir.setFont(ThemeManager.TEXT_SUBTITLE);
        bSalir.setForeground(ThemeManager.COLOR_TEXT);
        bSalir.setBackground(ThemeManager.COLOR_PRIMARY);
        bSalir.setBorder(BorderFactory.createEmptyBorder());
    }

    MenuInicioSesion(){
        SetPosition();
        SetTheme();

    }

}
