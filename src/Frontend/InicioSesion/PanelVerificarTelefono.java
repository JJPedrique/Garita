import java.awt.*;
import java.io.File;
import javax.swing.*;
import Backend.ThemeManager;

public class PanelVerificarTelefono extends JPanel {
    
    private final int BORDER_RADIUS_PX = 16;
    private final int ICON_WIDTH_PX = 24;
    private final int ICON_HEIGHT_PX = 24;
    
    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("RECUPERAR CLAVE");

    private final Icon iconRegreso = SetImgIcon("img\\go_back.png", ICON_WIDTH_PX*2, ICON_HEIGHT_PX*2);
    private final JButton bRegresar = JB_Regreso();
    
    private final JPanel pInput = new JPanel();
    private final JLabel lSubTitulo = new JLabel("<html><center>Ingrese su Número Telefónico <br> para recibir un código.</center></html>");
    private final JLabel lInputTelefono = new RoundIconLabel("img\\phone.png");
    private final JTextField tfInputTelefono = TF_Telefono(" 0414-1234567");

    private final JPanel pButton = new JPanel();
    private final JButton bRecibirCodigo = JB_Default("Recibir Código");
    


    //region Panel
    public PanelVerificarTelefono() {

        // Panel Base
        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.gridx = 0; GBC.gridy = 0; GBC.weightx = 1.0; GBC.weighty = 0.0; GBC.ipady = 40; add(pHeader, GBC);

        GBC.fill = GridBagConstraints.BOTH;
        GBC.gridx = 0; GBC.gridy = 1; GBC.weightx = 1.0; GBC.weighty = 1.0; add(pInput, GBC);

        GBC.gridx = 0; GBC.gridy = 2; GBC.weightx = 1.0; GBC.weighty = 0.0; GBC.ipady = 50; add(pButton, GBC);

        // Panel Encabezado
        pHeader.setLayout(GBL);
        GridBagConstraints GBC_Regreso = new GridBagConstraints(); // Esto hace que el boton de regreso se ponga en toda la izquieda
        GBC_Regreso.anchor = GridBagConstraints.FIRST_LINE_START; 
        GBC_Regreso.insets = new Insets(10, 10, 0, 0);
        GBC_Regreso.gridx = 0; GBC_Regreso.gridy = 0; GBC_Regreso.weightx = 0.0; GBC_Regreso.weighty = 0.0; 
        pHeader.add(bRegresar, GBC_Regreso);

        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.anchor = GridBagConstraints.CENTER; 
        GBC.gridx = 0; GBC.gridy = 0; GBC.weightx = 1.0; GBC.weighty = 0.0;
        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER); 
        pHeader.add(lHeaderTitle, GBC);

        // Panel Entrada de Datos
        pInput.setLayout(GBL);
        GBC.anchor = GridBagConstraints.CENTER;
        GBC.ipady=0; GBC.ipadx=0; 
        
        GBC.gridwidth=2;
        lSubTitulo.setHorizontalAlignment(JLabel.CENTER);
        GBC.insets = new Insets(20, 0, 20, 0); 
        GBC.gridx=0; GBC.gridy=0; GBC.weighty=0.0; pInput.add(lSubTitulo, GBC);
        
        GBC.gridwidth=1; GBC.weightx=0.0;

        GBC.insets = new Insets(6, 48, 8, 8);
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=0.0; pInput.add(lInputTelefono, GBC);

        GBC.insets = new Insets(6, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=1; GBC.weighty=0.0;  pInput.add(tfInputTelefono, GBC);

        GBC.gridwidth=1; GBC.weightx=1.0;

        pButton.setLayout(GBL);

        GBC.insets = new Insets(8, 30, 30, 30);
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=1.0;  pButton.add(bRecibirCodigo, GBC);

        SetTheme();
        SetupEvents();
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

    private void SetupEvents() {
        bRecibirCodigo.addActionListener(e -> {

            String telefono = tfInputTelefono.getText().trim();

            if (telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, introduzca su teléfono","Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VERIFICAR BDD - SI TELEFONO NO EXISTE, RETURN

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

        bRegresar.addActionListener(e -> {
            Container parent = this.getParent();
            if (parent != null) {
                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (ventanaPadre != null) {
                        ventanaPadre.remove(this); 
                        ventanaPadre.add(new MenuInicioSesion());
                        ventanaPadre.revalidate();
                        ventanaPadre.repaint();
                    }
            } else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
            }
        });
    }

    //region Helper Functions
    private ImageIcon SetImgIcon(String resourcePath, int width, int height) {
        try {
            File file = new File(resourcePath);
            if (file.exists()) {
                ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el icono " + resourcePath + ": " + e.getMessage());
        }
        return null;
    }

    private JButton JB_Default(String texto) {
        JButton JB = new JButton(texto) {
            @Override
            //JButton - Border Radius
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS_PX, BORDER_RADIUS_PX);
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

        //JButton - Hover
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


    private JTextField TF_Telefono(String placeholder) {
        JTextField TF = new JTextField("") {
            @Override
            protected void paintComponent(Graphics g) {
                // Input - Border Radius
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, BORDER_RADIUS_PX, BORDER_RADIUS_PX); 
                g2.dispose();
                super.paintComponent(g);

                // Placeholder - Campo Vacio o Sin Focus
                if (getText().isEmpty()) {
                    Graphics2D gPlaceholder = (Graphics2D) g.create();
                    gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gPlaceholder.setColor(ThemeManager.COLOR_PLACEHOLDER);
                    gPlaceholder.setFont(getFont());
                    
                    // Calcular centrado vertical basándonos en las fuentes e insets
                    FontMetrics fm = gPlaceholder.getFontMetrics();
                    Insets insets = getInsets();
                    int x = insets.left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    gPlaceholder.drawString(placeholder, x, y);
                    gPlaceholder.dispose();
                }
            }
        };

        TF.setPreferredSize(new Dimension(250, 35));
        TF.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        TF.setOpaque(false);
        TF.setFont(ThemeManager.TEXT_NORMAL);
        TF.setBackground(ThemeManager.COLOR_INPUT);
        TF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        TF.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        // Repintar al ganar o perder foco para refrescar el placeholder
        TF.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {TF.repaint(); }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) { TF.repaint(); }
        });

        return TF;
    }

    // Soportar imágenes PNG redondeadas
    private class RoundIconLabel extends JLabel {
        private Icon customIcon = null;

        public RoundIconLabel(String iconPath) {
            super("", SwingConstants.CENTER);
            setOpaque(false);
            setPreferredSize(new Dimension(35, 35));
            setMinimumSize(new Dimension(35, 35));
            setMaximumSize(new Dimension(35, 35));

            ImageIcon imgIcon = SetImgIcon(iconPath, ICON_WIDTH_PX, ICON_HEIGHT_PX);
            if (imgIcon != null) this.customIcon = imgIcon;
            
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fondo circular
            g2.setColor(ThemeManager.COLOR_LABEL);
            g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
            
            // Poner el ícono centrado
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