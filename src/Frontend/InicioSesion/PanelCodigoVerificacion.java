package Frontend.InicioSesion;

import java.awt.*;
import javax.swing.*;
import Backend.ThemeManager;

import java.util.Random;
public class PanelCodigoVerificacion extends JPanel{
    int randCode;
    Random r = new Random();
    
    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle= new JLabel("VERIFICAR CÓDIGO");

    private final Icon iconRegreso = ThemeManager.SetImgIcon("img\\go_back.png", ThemeManager.ICON_WIDTH_PX*2, ThemeManager.ICON_HEIGHT_PX*2);
    private final JButton bRegresar = ThemeManager.Button("");
    
    private final JPanel pInput = new JPanel();
    private final JLabel lSubTitulo = new JLabel("<html><center>Ingrese su código de verificación de<br> 6 dígitos  .</center></html>");
    private final JLabel lInputClave = new ThemeManager.RoundIconLabel("img\\candado.png");
    //<a href="https://www.flaticon.es/iconos-gratis/candado" title="candado iconos">Candado iconos creados por feen - Flaticon</a>
    // GRACIAS "FEEN" POR TU APORTACIÓN DEL CANDADO, TE DEBO MI CULITO OWO
    private final JTextField tfInputClave = ThemeManager.Textfield();

    private final JPanel pButton = new JPanel();
    private final JButton bRecibirCodigo = ThemeManager.Button("Recibir Código");

    PanelCodigoVerificacion(){

        randCode = r.nextInt(1000000);
        System.out.println(randCode);

        // Panel Base
        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.gridx = 0; GBC.gridy = 0; GBC.weightx = 1.0; GBC.weighty = 0.0; GBC.ipady = 40; add(pHeader, GBC);

        GBC.fill = GridBagConstraints.BOTH;
        GBC.gridx = 0; GBC.gridy = 1; GBC.weightx = 1.0; GBC.weighty = 1.0; add(pInput, GBC);

        GBC.gridx = 0; GBC.gridy = 2; GBC.weightx = 1.0; GBC.weighty = 0.0; GBC.ipady = 50; add(pButton, GBC);

        // Panel Encabezado
        lHeaderTitle.setText(Integer.toString(randCode));

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
        GBC.gridx=0; GBC.gridy=1; GBC.weighty=0.0; pInput.add(lInputClave, GBC);

        GBC.insets = new Insets(6, 8, 8, 48);
        GBC.gridx=1; GBC.gridy=1; GBC.weighty=0.0;  pInput.add(tfInputClave, GBC);

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
        bRecibirCodigo.addActionListener(e -> {

            String Clave = tfInputClave.getText().trim();

            if (Clave.isEmpty()) {
                ThemeManager.MostrarMensajeError(this, "Por favor, introduzca la clave");
                return;
            }

            if(!Clave.equals(Integer.toString(randCode))){
                ThemeManager.MostrarMensajeError(this, "Por favor, introduzca la clave correcta");
                return;
            }

            // SI Clave NO EXISTE, RETURN

            Container parent = this.getParent();
            if (parent != null) {
                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (ventanaPadre != null) {
                        ventanaPadre.remove(this); 
                        ventanaPadre.add(new PanelRestaurarClave());
                        ventanaPadre.revalidate();
                        ventanaPadre.repaint();
                    }
            } else {
                System.err.println("Error: El panel actual no está contenido en ningún componente padre.");
            }
        });

            bRegresar.addActionListener(e -> {
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
}