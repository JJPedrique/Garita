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
    private final JLabel lInputUsuario = new ThemeManager.RoundIconLabel("img\\user.png");
    private final JTextField tfInputUsuario = ThemeManager.Textfield();
    private final JLabel lInputClave = new ThemeManager.RoundIconLabel("img\\key.png");
    private final JPasswordField pfClave = ThemeManager.PasswordField();
    private final JToggleButton tbMostrarClave = ThemeManager.ToggleButton();
    private Icon iconShowPW = ThemeManager.SetImgIcon("img\\show_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private Icon iconHidePW = ThemeManager.SetImgIcon("img\\hide_pw.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX);
    private final JButton bOlvidaClave = ThemeManager.Button("¿Olvidó su clave?");

    private final JPanel pButton = new JPanel();
    private final JButton bAcceder = ThemeManager.Button("Acceder");
    private final JButton bSalir = ThemeManager.Button("Salir");

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

        bOlvidaClave.setMaximumSize(new Dimension(95, 12));
        bOlvidaClave.setForeground(ThemeManager.COLOR_TEXT);
        bOlvidaClave.setBackground(ThemeManager.COLOR_BACKGROUND);
        bOlvidaClave.setOpaque(false);
        bOlvidaClave.setFont(ThemeManager.TEXT_NORMAL);
        bOlvidaClave.setFocusPainted(false);
        bOlvidaClave.setBorderPainted(false);
        bOlvidaClave.setAlignmentX(Component.CENTER_ALIGNMENT);
        bOlvidaClave.setHorizontalAlignment(SwingConstants.CENTER);
        bOlvidaClave.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bOlvidaClave.setBackground(ThemeManager.COLOR_BACKGROUND);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bOlvidaClave.setBackground(ThemeManager.COLOR_BACKGROUND);
            }
        });
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
                ThemeManager.MostrarMensajeError(this, "Por favor, introduzca su usuario y su contraseña.");
                return;
            }
            if (usuario.isEmpty()) {
                ThemeManager.MostrarMensajeError(this, "El campo de usuario no puede quedar vacío.");
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            if (clave.isEmpty()) {
                ThemeManager.MostrarMensajeError(this, "El campo de clave no puede quedar vacío.");
                pfClave.requestFocusInWindow();
                return;
            }

            if (!usuario.matches("(?i)^V-[1-9]\\d{0,7}$")){
                ThemeManager.MostrarMensajeError(this, "Formato de usuario invalido. \nSiga el siguiente ejemplo: V-12345678");
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            ValidarInicioSesion(usuario, clave);

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
    private void ValidarInicioSesion(String usuario, String clave){
        try {
            ConexionPostgres BDD = new ConexionPostgres();
            
            ResultSet RS = BDD.consultar(
                "SELECT id, concat(nombre,' ',apellido) AS nombre_completo, intentos_fallidos, clave FROM usuarios WHERE cedula = ? LIMIT 1;",
                new Object[]{usuario}
            );

            ArrayList<Object> T = new ArrayList<>();
            while(RS != null && RS.next()){
                T.add(RS.getInt("id"));
                T.add(RS.getString("nombre_completo"));
                T.add(RS.getInt("intentos_fallidos"));
                T.add(RS.getString("clave"));
            }

            if(T.isEmpty()){
                ThemeManager.MostrarMensajeError(this, "Usuario o Clave incorrecto.");
                return;
            }

            int intentosActuales = Integer.parseInt(T.get(2).toString());
            String claveCorrectaBD = T.get(3).toString();

            if(intentosActuales >= 3){
                ThemeManager.MostrarMensajeError(this, "Cuenta restringida por número máximo de intentos.\nConsulte con el administrador o restaure su contraseña.");
                return;
            }

            if (!claveCorrectaBD.equals(clave)) {
                BDD.comandoDML("UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE cedula = ?", new Object[]{usuario});
                
                if ((intentosActuales + 1) > 3) {
                    ThemeManager.MostrarMensajeError(this, "Has alcanzado el límite de 3 intentos. Tu cuenta ha sido bloqueada.");
                } else {
                    ThemeManager.MostrarMensajeError(this, "Usuario o Clave incorrecto.");
                }
                return;
            }

            ThemeManager.MostrarMensajeExito(this, "¡Inicio de sesión exitoso!\n Bienvenido/a, " + T.get(1) + ".");
            
            JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (ventanaPadre != null) {
                ventanaPadre.remove(this);
                ventanaPadre.dispose();
                JFrame window = new JFrame("Sistema Garita - Menú Principal");

                window.setSize(1600,900);
                window.setMinimumSize(new Dimension(1600,900));
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    window.add(new MenuPrincipal());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                window.setVisible(true);
            }
            else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
                return;
            }

            BDD.comandoDML("UPDATE usuarios SET intentos_fallidos = 0 WHERE cedula = ?;", new Object[]{usuario});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}