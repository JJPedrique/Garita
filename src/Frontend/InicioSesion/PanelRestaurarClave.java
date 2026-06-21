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
    private final JButton bRegresar = JB_Regreso();

    private final JPanel pInput = new JPanel();

    private final JLabel lNuevaClave = new RoundIconLabel("img\\key.png");
    private final JLabel lSubTituloNuevaClave = new JLabel("Nueva Clave");
    private final JPasswordField pfInputpwrd = PF_Password("********");
    private final JToggleButton tbMostrarClave1 = TGB_ShowPassword();

    private final JLabel lConfirmarClave = new RoundIconLabel("img\\key.png");
    private final JLabel lSubtituloConfirmarClave = new JLabel("Confirmar Clave");
    private final JPasswordField pfConfirmarClave = PF_Password("********");
    private final JToggleButton tbMostrarClave2 = TGB_ShowPassword();

    private Icon iconShowPW = ThemeManager.SetImgIcon("img\\show_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private Icon iconHidePW = ThemeManager.SetImgIcon("img\\hide_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);

    private final JPanel pButton = new JPanel();
    private final JButton bRestaurar = JB_Default("Restaurar");

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
    }

    private void SetEvents() { 
        bRestaurar.addActionListener(e -> {
            String nuevaClave = new String(pfInputpwrd.getPassword()).trim();
            String confirmarClave = new String(pfConfirmarClave.getPassword()).trim();

            if (nuevaClave.isEmpty() || confirmarClave.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete ambos campos de contraseña.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            } 
            
            if (!nuevaClave.equals(confirmarClave)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden. Por favor, verifíquelas.", "Error de Coincidencia", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String Query = "UPDATE usuarios SET clave = ? WHERE id = ?;";
            Object Parametros[] = {nuevaClave,PanelVerificarTelefono.idUsuario};
            try {
                ConexionPostgres BDD = new ConexionPostgres();
                BDD.comandoDML(Query,Parametros);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "¡Clave restaurada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
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

    private JButton JB_Default(String texto) {
        JButton JB = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ThemeManager.BORDER_RADIUS_PX, ThemeManager.BORDER_RADIUS_PX);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        JB.setMaximumSize(new Dimension(175, 50));
        JB.setForeground(ThemeManager.COLOR_TEXT);
        JB.setBackground(ThemeManager.COLOR_PRIMARY);
        JB.setFont(ThemeManager.TEXT_SUBTITLE);
        JB.setFocusPainted(false);
        JB.setBorderPainted(false);
        JB.setContentAreaFilled(false);
        JB.setAlignmentX(Component.CENTER_ALIGNMENT);
        JB.setHorizontalAlignment(SwingConstants.CENTER);

        JB.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                JB.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                JB.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });

        return JB;
    }

    private JButton JB_Regreso() {
        JButton JB = new JButton();
        JB.setIcon(iconRegreso);
        JB.setOpaque(false);
        JB.setContentAreaFilled(false); 
        JB.setBorderPainted(false);     
        JB.setFocusPainted(false);     
        JB.setMaximumSize(new Dimension(50, 50));
        JB.setPreferredSize(new Dimension(50, 50)); 
        JB.setAlignmentX(Component.CENTER_ALIGNMENT);
        JB.setHorizontalAlignment(SwingConstants.CENTER);
        return JB;
    }

    private JToggleButton TGB_ShowPassword() {
        JToggleButton JTGB = new JToggleButton();
        JTGB.setPreferredSize(new Dimension(40, 35));
        JTGB.setOpaque(false);
        JTGB.setContentAreaFilled(true);
        JTGB.setBackground(ThemeManager.COLOR_INPUT);
        JTGB.setBorderPainted(false);
        JTGB.setFocusPainted(false);
        JTGB.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return JTGB;
    }

    private JPasswordField PF_Password(String placeholder) {
        JPasswordField PF = new JPasswordField("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1 , getHeight()-1, ThemeManager.BORDER_RADIUS_PX, ThemeManager.BORDER_RADIUS_PX);
                g2.dispose();
                super.paintComponent(g);

                if (getPassword().length == 0) {
                    Graphics2D gPlaceholder = (Graphics2D) g.create();
                    gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gPlaceholder.setColor(Color.GRAY);
                    gPlaceholder.setFont(getFont());
                    
                    FontMetrics fm = gPlaceholder.getFontMetrics();
                    Insets insets = getInsets();
                    int x = insets.left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    gPlaceholder.drawString(placeholder, x, y);
                    gPlaceholder.dispose();
                }
            }
        };
        
        PF.setPreferredSize(new Dimension(250, 35));
        PF.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 1)); 
        PF.setOpaque(false);
        PF.setFont(ThemeManager.TEXT_NORMAL);
        PF.setBackground(ThemeManager.COLOR_INPUT);
        PF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        PF.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        PF.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) { PF.repaint(); }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) { PF.repaint(); }
        });
        
        return PF;
    }

    private class RoundIconLabel extends JLabel {
        private Icon customIcon = null;

        public RoundIconLabel(String iconPath) {
            super("", SwingConstants.CENTER);
            setOpaque(false);
            setPreferredSize(new Dimension(35, 35));
            setMinimumSize(new Dimension(35, 35));
            setMaximumSize(new Dimension(35, 35));

            ImageIcon imgIcon = ThemeManager.SetImgIcon(iconPath, ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
            if (imgIcon != null) this.customIcon = imgIcon;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeManager.COLOR_LABEL);
            g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
            
            if (customIcon != null) {
                int iconWidth = customIcon.getIconWidth();
                int iconHeight = customIcon.getIconHeight();
                int x = (getWidth()-iconWidth) / 2;
                int y = (getHeight()-iconHeight) / 2;
                customIcon.paintIcon(this, g2, x, y);
            }
            g2.dispose();
        }
    }
}
