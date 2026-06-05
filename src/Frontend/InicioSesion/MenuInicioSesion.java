import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

/*
PENDIENTE:
* Corregir ToogleButton
* Meter los Iconos en los labels y al ToggleButton
* Meterle eventos a los botones
* Crear el modulo de restauracion de clave
*/

public class MenuInicioSesion extends JPanel {
    
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("SISTEMA GARITA");

    private final JPanel pInput = new JPanel();
    private final JLabel lSubTitulo = new JLabel("INICIAR SESIÓN");
    private final JLabel lInputUsuario = new RoundIconLabel("U");
    private final JTextField tfInputUsuario = TF_Username("Usuario");
    private final JLabel lInputClave = new RoundIconLabel("C");
    private final JPasswordField pfClave = PF_Password();
    private final JToggleButton tbMostrarClave = TG_ShowPassword("S");
    private final JButton bOlvidaClave = JB_ForgottenPassword("¿Olvidó su clave?");

    private final JPanel pButton = new JPanel();
    private final JButton bAcceder = JB("Acceder");
    private final JButton bSalir = JB("Salir");

    public MenuInicioSesion() {

        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.anchor = GridBagConstraints.CENTER;
        addJComponent(this, pHeader, 0, 0, 1, 1, new Insets(0, 0, 0, 0), 0, 40, 1.0, 0.0);
        addJComponent(this, pInput, 0, 1, 1, 1, new Insets(0, 0, 0, 0), 0, 0, 1.0, 1.0);
        addJComponent(this, pButton, 0, 2, 1, 1, new Insets(0, 0, 0, 0), 0, 30, 1.0, 0.0);


        pHeader.setLayout(GBL);
        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER);
        addJComponent(pHeader, lHeaderTitle, 0, 0, 1, 1, new Insets(0, 0, 0, 0), 0, 0, 1.0, 0.0);


        pInput.setLayout(GBL);
        lSubTitulo.setHorizontalAlignment(JLabel.CENTER);
        addJComponent(pInput, lSubTitulo, 0, 0, 2, 1, new Insets(20, 0, 20, 0), 0, 0, 1.0, 0.0);
        addJComponent(pInput, lInputUsuario, 0, 1, 1, 1, new Insets(6, 48, 8, 8), 0, 0, 0.0, 0.0);
        addJComponent(pInput, tfInputUsuario, 1, 1, 1, 1, new Insets(6, 8, 8, 48), 0, 0, 0.0, 0.0);

        pfClave.setLayout(new BorderLayout());

        char echoCharDefault = pfClave.getEchoChar();
        tbMostrarClave.addActionListener(e -> {
            if (tbMostrarClave.isSelected()) {
                pfClave.setEchoChar((char) 0); 
                tbMostrarClave.setText("H");
            } else {
                pfClave.setEchoChar(echoCharDefault); 
                tbMostrarClave.setText("S");
            }
        });
        pfClave.add(tbMostrarClave, BorderLayout.EAST);

        addJComponent(pInput, lInputClave, 0, 2, 1, 1, new Insets(8, 48, 8, 8), 0, 0, 0.0, 0.0);
        addJComponent(pInput, pfClave, 1, 2, 1, 1, new Insets(8, 8, 8, 48), 0, 0, 0.0, 0.0);
        addJComponent(pInput, bOlvidaClave, 0, 3, 2, 1, new Insets(8, 48, 48, 48), 0, 0, 1.0, 0.0);


        pButton.setLayout(GBL);
        addJComponent(pButton, bAcceder, 0, 1, 1, 1, new Insets(4, 30, 8, 30), 0, 0, 1.0, 1.0);
        addJComponent(pButton, bSalir, 0, 2, 1, 1, new Insets(8, 30, 30, 30), 0, 0, 1.0, 1.0);

        SetTheme();
    }

    public void SetTheme() {
        setBackground(ThemeManager.COLOR_BACKGROUND);
        pHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pInput.setOpaque(false);
        pButton.setOpaque(false);

        lHeaderTitle.setFont(ThemeManager.TEXT_TITLE);
        lHeaderTitle.setForeground(ThemeManager.COLOR_TEXT);

        lSubTitulo.setFont(ThemeManager.TEXT_SUBTITLE);
        lSubTitulo.setForeground(ThemeManager.COLOR_TEXT);
    }

    private void addJComponent(JPanel JParent, JComponent JChild, int X, int Y, int ColSpan, int RowSpan, Insets Margin, int PadX, int PadY, double WeightX, double WeightY) {
        GBC.gridx = X;          
        GBC.gridy = Y;
        GBC.gridwidth = ColSpan;    
        GBC.gridheight = RowSpan;
        GBC.ipadx = PadX;       
        GBC.ipady = PadY;
        GBC.weightx = WeightX;    
        GBC.weighty = WeightY;
        GBC.insets = Margin;     
        JParent.add(JChild, GBC);
    }

    private JButton JB(String texto) {
        JButton JB = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
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

    private JButton JB_ForgottenPassword(String texto) {
        JButton button = new JButton(texto);
        button.setMaximumSize(new Dimension(95, 12));
        button.setForeground(ThemeManager.COLOR_TEXT);
        button.setBackground(ThemeManager.COLOR_BACKGROUND);
        button.setOpaque(false);
        button.setFont(ThemeManager.TEXT_NORMAL);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ThemeManager.COLOR_BACKGROUND);
            }
        });

        return button;
    }

    private JToggleButton TG_ShowPassword(String ico) {
        JToggleButton JTG = new JToggleButton(ico);
        JTG.setPreferredSize(new Dimension(45, 50));
        JTG.setOpaque(false);
        JTG.setContentAreaFilled(false);
        JTG.setBorderPainted(false);
        JTG.setFocusPainted(false);
        JTG.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return JTG;
    }

    private JTextField TF_Username(String text) {
        JTextField TF = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        TF.setPreferredSize(new Dimension(250, 35));
        TF.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        TF.setOpaque(false);
        TF.setFont(ThemeManager.TEXT_NORMAL);
        TF.setBackground(ThemeManager.COLOR_INPUT);
        TF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        TF.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        return TF;
    }

    private JPasswordField PF_Password() {
        JPasswordField PF = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        PF.setPreferredSize(new Dimension(250, 35));
        PF.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 1));
        PF.setOpaque(false);
        PF.setFont(ThemeManager.TEXT_NORMAL);
        PF.setBackground(ThemeManager.COLOR_INPUT);
        PF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        PF.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        return PF;
    }

    private class RoundIconLabel extends JLabel {
        public RoundIconLabel(String iconText) {
            super(iconText, SwingConstants.CENTER);
            setOpaque(false);
            setPreferredSize(new Dimension(35, 35));
            setMinimumSize(new Dimension(35, 35));
            setMaximumSize(new Dimension(35, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(getBackground());
            g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
            
            g2.dispose();
            
            super.paintComponent(g);
        }
    }
}