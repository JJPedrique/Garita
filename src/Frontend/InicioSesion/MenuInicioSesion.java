package Frontend.InicioSesion;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import Frontend.MenuPrincipal;

public class MenuInicioSesion extends JPanel {
    
    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("SISTEMA GARITA");

    private final JPanel pInput = new JPanel();
    private final JLabel lSubTitulo = new JLabel("INICIAR SESIÓN");
    private final JLabel lInputUsuario = new RoundIconLabel("img\\user.png");
    private final JTextField tfInputUsuario = TF_Username("V-12345678");
    private final JLabel lInputClave = new RoundIconLabel("img\\key.png");
    private final JPasswordField pfClave = PF_Password("********");
    private final JToggleButton tbMostrarClave = TGB_ShowPassword();
    private Icon iconShowPW = ThemeManager.SetImgIcon("img\\show_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private Icon iconHidePW = ThemeManager.SetImgIcon("img\\hide_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private final JButton bOlvidaClave = JB_ForgottenPassword("¿Olvidó su clave?");

    private final JPanel pButton = new JPanel();
    private final JButton bAcceder = JB_Default("Acceder");
    private final JButton bSalir = JB_Default("Salir");

    public static int idUsuario;

    //region Panel
    public MenuInicioSesion() {

        // Layout Base de los paneles
        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.anchor = GridBagConstraints.CENTER;

        GBC.weightx=1.0;
        GBC.gridx=0; GBC.gridy=0; GBC.ipady=40; GBC.weighty=0.0; add(pHeader, GBC);
        GBC.gridx=0; GBC.gridy=1; GBC.ipady=0; GBC.weighty=1.0; add(pInput, GBC);
        GBC.gridx=0; GBC.gridy=2; GBC.ipady=30; GBC.weighty=0.0; add(pButton, GBC);

        // Panel Header
        pHeader.setLayout(GBL);
        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER);
        GBC.gridx=0; GBC.gridy=0; GBC.ipady=0; GBC.weighty=0.0; pHeader.add(lHeaderTitle, GBC);

        // Panel Entrada de datos
        pInput.setLayout(GBL);
        GBC.gridwidth=2;
        lSubTitulo.setHorizontalAlignment(JLabel.CENTER);
        GBC.insets = new Insets(20, 0, 20, 0);
        GBC.gridx=0; GBC.gridy=0; GBC.weighty=0.0; pInput.add(lSubTitulo, GBC);
        
        GBC.gridwidth=1; GBC.weightx=0.0;

        GBC.insets = new Insets(6, 48, 8, 8);
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=0.0; pInput.add(lInputUsuario, GBC);

        GBC.insets = new Insets(6, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=1; GBC.weighty=0.0; pInput.add(tfInputUsuario, GBC);

        pfClave.setLayout(new BorderLayout());
        tbMostrarClave.setIcon(iconHidePW);

        // Togglebutton - Mostrar/Ocultar clave
        char echoCharDefault = pfClave.getEchoChar();
        tbMostrarClave.addActionListener(e -> {
            if (tbMostrarClave.isSelected()) {
                pfClave.setEchoChar((char) 0); 
                tbMostrarClave.setIcon(iconShowPW);
            } else {
                pfClave.setEchoChar(echoCharDefault); 
                tbMostrarClave.setIcon(iconHidePW);
            }
        });
        pfClave.add(tbMostrarClave, BorderLayout.EAST);

        GBC.insets = new Insets(8, 48, 8, 8);
        GBC.gridx=0; GBC.gridy=2; GBC.weighty=0.0; pInput.add(lInputClave, GBC);

        GBC.insets = new Insets(8, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=2; GBC.weighty=0.0; pInput.add(pfClave, GBC);

        GBC.gridwidth=2; GBC.weightx=1.0;

        GBC.insets = new Insets(8, 48, 48, 48);
        GBC.gridx=0; GBC.gridy=3; GBC.weighty=0.0; pInput.add(bOlvidaClave, GBC);

        // Panel Botones
        pButton.setLayout(GBL);
        GBC.gridwidth=1; GBC.weightx=1.0;

        GBC.insets = new Insets(4, 30, 8, 30);
        GBC.gridx=0; GBC.gridy=0; GBC.weighty=1.0; pButton.add(bAcceder, GBC);

        GBC.insets = new Insets(8, 30, 30, 30);
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=1.0; pButton.add(bSalir, GBC);

        SetTheme();
        SetEvents();
    }

    public void SetTheme() { // Aplicar Estilo, los colores vienen de la clase ThemeManager
        setBackground(ThemeManager.COLOR_BACKGROUND);
        pHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pInput.setOpaque(false);
        pButton.setOpaque(false);

        lHeaderTitle.setFont(ThemeManager.TEXT_TITLE);
        lHeaderTitle.setForeground(ThemeManager.COLOR_TEXT);

        lSubTitulo.setFont(ThemeManager.TEXT_SUBTITLE);
        lSubTitulo.setForeground(ThemeManager.COLOR_TEXT);
    }

    private void SetEvents() { // Configurar los eventos en los botones
        //region Boton Salir
        bSalir.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                this, "¿Está seguro que desea salir del Sistema Garita?",  "Confirmar Salida", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            if (confirmacion == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        //region Boton Acceder
        bAcceder.addActionListener(e -> {
            String usuario = tfInputUsuario.getText().trim().toUpperCase();
            String clave = new String(pfClave.getPassword()).trim();

            if (usuario.isEmpty() && clave.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, introduzca su usuario y su contraseña.","Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (usuario.isEmpty()) {
                JOptionPane.showMessageDialog(this,"El campo de usuario no puede quedar vacío.","Usuario Requerido",JOptionPane.WARNING_MESSAGE);
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            if (clave.isEmpty()) {
                JOptionPane.showMessageDialog(this,"El campo de clave no puede quedar vacío.","Clave Requerida", JOptionPane.WARNING_MESSAGE);
                pfClave.requestFocusInWindow();
                return;
            }

            if (!usuario.matches("(?i)^V-[1-9]\\d{0,7}$")){
                JOptionPane.showMessageDialog(this,"Formato de usuario invalido. \nSiga el siguiente ejemplo: V-12345678","Usuario Inválido", JOptionPane.WARNING_MESSAGE);
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            String Query = "SELECT id,concat(nombre,' ',apellido) AS nombre_completo FROM usuarios WHERE cedula = ? AND clave = ? LIMIT 1;";
            Object Parametros[] = {usuario,clave};
            try {
                ConexionPostgres BDD = new ConexionPostgres();
                ResultSet RS = BDD.consultar(Query,Parametros);

                ArrayList<Object> TUPLA = new ArrayList<>();
                while(RS != null && RS.next()){
                    TUPLA.add(RS.getInt("id"));
                    TUPLA.add(RS.getString("nombre_completo"));
                }

                if(TUPLA.isEmpty()){
                    JOptionPane.showMessageDialog(this,"Clave o Usuario incorrecto.","ERROR",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this,"¡Inicio de sesión exitoso!\nBienvenido, "+TUPLA.get(1)+".","Acceso Concedido",JOptionPane.INFORMATION_MESSAGE);
                
                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (ventanaPadre != null) {
                    ventanaPadre.remove(this);
                    ventanaPadre.dispose();
                    JFrame window = new JFrame("Sistema Garita - Menú Principal");
                    window.setSize(1600,900);
                    window.setMinimumSize(new Dimension(1600,900));
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    window.add(new MenuPrincipal());
                    window.setVisible(true);
                }
                else {
                    System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
                    return;
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
                return;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        //region Clave olvidada
        bOlvidaClave.addActionListener(e -> {
            JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (ventanaPadre != null) {
                ventanaPadre.remove(this); 
                ventanaPadre.add(new PanelVerificarTelefono());
                ventanaPadre.revalidate();
                ventanaPadre.repaint();
            }
            else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
            }
        });
    }

    //region Helper Functions
    private JButton JB_Default(String texto) {
        JButton JB = new JButton(texto) {
            @Override
            //JButton - Border Radius
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

        //JButton - Hover
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

    private JTextField TF_Username(String placeholder) {
        JTextField TF = new JTextField("") {
            @Override
            protected void paintComponent(Graphics g) {
                // Input - Border Radius
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, ThemeManager.BORDER_RADIUS_PX, ThemeManager.BORDER_RADIUS_PX); 
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

    private JPasswordField PF_Password(String placeholder) {
        JPasswordField PF = new JPasswordField("") {
            @Override
            protected void paintComponent(Graphics g) {
                // Input - Border Radius
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1 , getHeight()-1, ThemeManager.BORDER_RADIUS_PX, ThemeManager.BORDER_RADIUS_PX);
                g2.dispose();
                super.paintComponent(g);

                // Placeholder - Campo Vacio o Sin Focus
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

        // Repintar al ganar o perder foco para refrescar el placeholder
        PF.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) { PF.repaint(); }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) { PF.repaint(); }
        });
        
        return PF;
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

            ImageIcon imgIcon = ThemeManager.SetImgIcon(iconPath, ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
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