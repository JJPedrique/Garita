package Frontend.InicioSesion;

import java.awt.*;
import java.sql.SQLException;

import javax.swing.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class PanelRestaurarClave extends JPanel{
    
    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("Restaurar Clave");
    
    private final Icon iconRegreso = ThemeManager.SetImgIcon("img\\go_back.png", ThemeManager.ICON_WIDTH_PX*2, ThemeManager.ICON_HEIGHT_PX*2);
    private final JButton bRegresar = ThemeManager.Button("");

    private final JPanel pInput = new JPanel();

    private final JLabel lNuevaClave = new ThemeManager.RoundIconLabel("img\\key.png");
    private final JLabel lSubTituloNuevaClave = new JLabel("Nueva Clave");
    private final JPasswordField pfInputpwrd = ThemeManager.PasswordField();
    private final JToggleButton tbMostrarClave1 = ThemeManager.ToggleButton();

    private final JLabel lConfirmarClave = new ThemeManager.RoundIconLabel("img\\key.png");
    private final JLabel lSubtituloConfirmarClave = new JLabel("Confirmar Clave");
    private final JPasswordField pfConfirmarClave = ThemeManager.PasswordField();
    private final JToggleButton tbMostrarClave2 = ThemeManager.ToggleButton();

    private Icon iconShowPW = ThemeManager.SetImgIcon("img\\show_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private Icon iconHidePW = ThemeManager.SetImgIcon("img\\hide_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);

    private final JPanel pButton = new JPanel();
    private final JButton bRestaurar = ThemeManager.Button("Restaurar");

    PanelRestaurarClave(){
        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.anchor = GridBagConstraints.CENTER;

        GBC.weightx = 1.0;
        GBC.gridx = 0; GBC.gridy = 0; GBC.ipady = 40; GBC.weighty = 0.0; add(pHeader, GBC);
        GBC.gridx = 0; GBC.gridy = 1; GBC.ipady = 0;  GBC.weighty = 1.0; add(pInput, GBC);
        GBC.gridx = 0; GBC.gridy = 2; GBC.ipady = 30; GBC.weighty = 0.0; add(pButton, GBC);

        pHeader.setLayout(GBL);
        
        GridBagConstraints GBC_Regreso = new GridBagConstraints();
        GBC_Regreso.anchor = GridBagConstraints.FIRST_LINE_START; 
        GBC_Regreso.insets = new Insets(10, 10, 0, 0);
        GBC_Regreso.gridx = 0; GBC_Regreso.gridy = 0; GBC_Regreso.weightx = 0.0; GBC_Regreso.weighty = 0.0; 
        pHeader.add(bRegresar, GBC_Regreso);

        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.anchor = GridBagConstraints.CENTER; 
        GBC.gridx = 0; GBC.gridy = 0; GBC.weightx = 1.0; GBC.weighty = 0.0; GBC.ipady = 0;
        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER); 
        pHeader.add(lHeaderTitle, GBC);

        pInput.setLayout(GBL);
        
        GBC.gridwidth = 2;
        GBC.weightx = 1.0;
        lSubTituloNuevaClave.setHorizontalAlignment(JLabel.CENTER);
        GBC.insets = new Insets(20, 0, 10, 0);
        GBC.gridx = 0; GBC.gridy = 0; GBC.weighty = 0.0; pInput.add(lSubTituloNuevaClave, GBC);
        
        GBC.gridwidth = 1; 
        GBC.weightx = 0.0;
        GBC.insets = new Insets(6, 48, 6, 8);
        GBC.gridx = 0; GBC.gridy = 1; pInput.add(lNuevaClave, GBC);

        GBC.weightx = 1.0;
        GBC.insets = new Insets(6, 8, 6, 48);
        GBC.gridx = 1; GBC.gridy = 1; pInput.add(pfInputpwrd, GBC);

        GBC.gridwidth = 2;
        GBC.weightx = 1.0;
        lSubtituloConfirmarClave.setHorizontalAlignment(JLabel.CENTER);
        GBC.insets = new Insets(15, 0, 10, 0);
        GBC.gridx = 0; GBC.gridy = 2; pInput.add(lSubtituloConfirmarClave, GBC);

        GBC.gridwidth = 1; 
        GBC.weightx = 0.0;
        GBC.insets = new Insets(6, 48, 20, 8);
        GBC.gridx = 0; GBC.gridy = 3; pInput.add(lConfirmarClave, GBC);

        GBC.weightx = 1.0;
        GBC.insets = new Insets(6, 8, 20, 48);
        GBC.gridx = 1; GBC.gridy = 3; pInput.add(pfConfirmarClave, GBC);

        pfInputpwrd.setLayout(new BorderLayout());
        pfConfirmarClave.setLayout(new BorderLayout());
        tbMostrarClave1.setIcon(iconHidePW);
        tbMostrarClave2.setIcon(iconHidePW);
        char echoCharDefault = pfConfirmarClave.getEchoChar();

        tbMostrarClave1.addActionListener(e -> {
            if (tbMostrarClave1.isSelected()) {
                pfInputpwrd.setEchoChar((char) 0); 
                tbMostrarClave1.setIcon(iconShowPW);
            } else {
                pfInputpwrd.setEchoChar(echoCharDefault); 
                tbMostrarClave1.setIcon(iconHidePW);
            }
        });
        pfInputpwrd.add(tbMostrarClave1, BorderLayout.EAST);

        tbMostrarClave2.addActionListener(e -> {
            if (tbMostrarClave2.isSelected()) {
                pfConfirmarClave.setEchoChar((char) 0); 
                tbMostrarClave2.setIcon(iconShowPW);
            } else {
                pfConfirmarClave.setEchoChar(echoCharDefault); 
                tbMostrarClave2.setIcon(iconHidePW);
            }
        });
        pfConfirmarClave.add(tbMostrarClave2, BorderLayout.EAST);

        pButton.setLayout(GBL);
        GBC.gridwidth = 1; GBC.weightx = 1.0;
        GBC.insets = new Insets(4, 30, 8, 30);
        GBC.gridx = 0; GBC.gridy = 0; GBC.weighty = 1.0; pButton.add(bRestaurar, GBC);

        SetTheme();
        SetEvents();
    }

    public void SetTheme() {
        setBackground(ThemeManager.COLOR_BACKGROUND);
        pHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pInput.setOpaque(false);
        pButton.setOpaque(false);

        lHeaderTitle.setFont(ThemeManager.TEXT_TITLE);
        lHeaderTitle.setForeground(ThemeManager.COLOR_TEXT);

        lSubTituloNuevaClave.setFont(ThemeManager.TEXT_SUBTITLE);
        lSubTituloNuevaClave.setForeground(ThemeManager.COLOR_TEXT);
        
        lSubtituloConfirmarClave.setFont(ThemeManager.TEXT_SUBTITLE);
        lSubtituloConfirmarClave.setForeground(ThemeManager.COLOR_TEXT);

        bRegresar.setIcon(iconRegreso);
        bRegresar.setOpaque(false);
        bRegresar.setContentAreaFilled(false); 
        bRegresar.setBorderPainted(false);     
        bRegresar.setFocusPainted(false);     
        
        bRegresar.setMaximumSize(new Dimension(50, 50));
        bRegresar.setPreferredSize(new Dimension(50, 50)); 
        bRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        bRegresar.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void SetEvents() { 
        bRestaurar.addActionListener(e -> {
            String nuevaClave = new String(pfInputpwrd.getPassword()).trim();
            String confirmarClave = new String(pfConfirmarClave.getPassword()).trim();

            if (nuevaClave.isEmpty() || confirmarClave.isEmpty()) {
                ThemeManager.MostrarMensajeError(this, "Por favor, complete ambos campos de contraseña.");
                return;
            } 
            
            if (!nuevaClave.equals(confirmarClave)) {
                ThemeManager.MostrarMensajeError(this, "Las contraseñas no coinciden. Por favor, verifíquelas.");
                return;
            }
            
            String Query = "UPDATE usuarios SET clave = ? WHERE id = ?;";
            Object Parametros[] = {nuevaClave,PanelVerificarTelefono.idUsuario};
            try {
                ConexionPostgres.comandoDML(Query,Parametros);
                
                ThemeManager.MostrarMensajeExito(this, "¡Clave restaurada con éxito!");
                ConexionPostgres.comandoDML("UPDATE usuarios SET intentos_fallidos = 0 WHERE id = ?;", new Object[]{PanelVerificarTelefono.idUsuario});
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (ventanaPadre != null) {
                ventanaPadre.remove(this); 
                ventanaPadre.add(new MenuInicioSesion());
                ventanaPadre.revalidate();
                ventanaPadre.repaint();
            }
            
        });

        bRegresar.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null) {
                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (ventanaPadre != null) {
                    ventanaPadre.remove(this); 
                    ventanaPadre.add(new PanelCodigoVerificacion()); 
                    ventanaPadre.revalidate();
                    ventanaPadre.repaint();
                }
            } else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
            }
        });
    }
}
