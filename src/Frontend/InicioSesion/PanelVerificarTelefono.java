package Frontend.InicioSesion;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class PanelVerificarTelefono extends JPanel {
    
    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("RECUPERAR CLAVE");

    private final Icon iconRegreso = ThemeManager.SetImgIcon("img\\go_back.png", ThemeManager.ICON_WIDTH_PX*2, ThemeManager.ICON_HEIGHT_PX*2);
    private final JButton bRegresar = ThemeManager.Button("");
    
    private final JPanel pInput = new JPanel();
    private final JLabel lSubTitulo = new JLabel("<html><center>Ingrese su usuario y número telefónico <br> para recibir un código.</center></html>");
    private final JLabel lInputUsuario = new ThemeManager.RoundIconLabel("img\\user.png");
    private final JTextField tfInputUsuario = ThemeManager.Textfield();
    private final JLabel lInputTelefono = new ThemeManager.RoundIconLabel("img\\phone.png");
    private final JTextField tfInputTelefono = ThemeManager.Textfield();

    private final JPanel pButton = new JPanel();
    private final JButton bRecibirCodigo = ThemeManager.Button("Recibir Código");

    public static int idUsuario;

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
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=0.0; pInput.add(lInputUsuario, GBC);

        GBC.insets = new Insets(6, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=1; GBC.weighty=0.0;  pInput.add(tfInputUsuario, GBC);

        GBC.insets = new Insets(6, 48, 8, 8);
        GBC.gridx=0; GBC.gridy=2; GBC.weighty=0.0; pInput.add(lInputTelefono, GBC);

        GBC.insets = new Insets(6, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=2; GBC.weighty=0.0;  pInput.add(tfInputTelefono, GBC);

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

    private void SetupEvents() {
        //region Recibir Codigo
        bRecibirCodigo.addActionListener(e -> {
            String usuario = tfInputUsuario.getText().trim().toUpperCase();
            String telefono = tfInputTelefono.getText().trim();

            if (usuario.isEmpty() && telefono.isEmpty()) {
                ThemeManager.MostrarMensajeError(this,"Por favor, introduzca su usuario y teléfono.");
                return;
            }

            if (usuario.isEmpty()) {
                ThemeManager.MostrarMensajeError(this,"Por favor, introduzca su usuario.");
                return;
            }

            if (telefono.isEmpty()) {
                ThemeManager.MostrarMensajeError(this,"Por favor, introduzca su teléfono.");
                return;
            }

            if (!usuario.matches("(?i)^V-[1-9]\\d{0,7}$")){
                ThemeManager.MostrarMensajeError(this,"Formato de usuario invalido. \nSiga el siguiente ejemplo: V-12345678");
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            if (!telefono.matches("^(0414|0424|0412|0416|0426|02\\d{2})-\\d{7}$")){
                ThemeManager.MostrarMensajeError(this,"Formato de teléfono invalido. \nSiga el siguiente ejemplo: 0424-1234567");
                tfInputUsuario.requestFocusInWindow();
                return;
            }

            String Query = "SELECT id FROM usuarios WHERE cedula = ? AND telefono = ? LIMIT 1";
            Object Parametros[] = {usuario,telefono};
            try {
                ResultSet RS = ConexionPostgres.consultar(Query,Parametros);

                ArrayList<Object> TUPLA = new ArrayList<>();
                while(RS != null && RS.next()){
                    TUPLA.add(RS.getInt("id"));
                }

                if(TUPLA.isEmpty()){
                    ThemeManager.MostrarMensajeError(this,"Usuario o Teléfono incorrecto.");
                    return;
                }

                idUsuario = Integer.parseInt(TUPLA.get(0).toString());
                ThemeManager.MostrarMensajeExito(this,"Datos verificados correctamente.");

                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (ventanaPadre != null) {
                    ventanaPadre.remove(this); 
                    ventanaPadre.add(new PanelCodigoVerificacion());
                    ventanaPadre.revalidate();
                    ventanaPadre.repaint();
                }
                else {
                    System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
                return;
            }
        });

        bRegresar.addActionListener(e -> {
            JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (ventanaPadre != null) {
                ventanaPadre.remove(this); 
                ventanaPadre.add(new MenuInicioSesion());
                ventanaPadre.revalidate();
                ventanaPadre.repaint();
            }
            else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
            }
        });
    }
}