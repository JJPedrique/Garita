package Frontend.ControlDeAcceso;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import Backend.ThemeManager.LimiteCaracteresFilter;

public class FrameSolicitarAccesoVisitante extends JPanel {

    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle = new JLabel("SOLICITAR ACCESO VISITANTE");

    private final JPanel pInput = new JPanel();
    private final JLabel lNombreVisitante = ThemeManager.Label("Nombre del visitante");
    private final JTextField tfNombreVisitante = ThemeManager.Textfield("Juan Boscan");
    
    private final JLabel lNombreResidente = ThemeManager.Label("Nombre del residente");
    private final JTextField tfNombreResidente = ThemeManager.Textfield("Carlos Mendoza");
    
    private final JPanel pPropietarios = new JPanel();
    ArrayList<JPanel> JPropietarios = new ArrayList<>();

    private final JPanel pButton = new JPanel();
    private final JButton bPermitirAcceso = ThemeManager.Button("Permitir Acceso");
    private final JButton bDenegarAcceso = ThemeManager.Button("Denegar Acceso");
    
    private JDialog JDPadre;
    //endregion

    public FrameSolicitarAccesoVisitante(JDialog JDPadre) {
        this.JDPadre = JDPadre;

        AbstractDocument AD;
        AD = (AbstractDocument) tfNombreResidente.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(40));

        AD = (AbstractDocument) tfNombreVisitante.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(40));

        // Layout Base
        setLayout(GBL);
        GBC.fill = GridBagConstraints.BOTH;
        GBC.anchor = GridBagConstraints.CENTER;

        GBC.weightx = 1.0;
        GBC.gridx = 0; GBC.gridy = 0; GBC.ipady = 50; GBC.weighty = 0.0; add(pHeader, GBC);
        GBC.gridx = 0; GBC.gridy = 1; GBC.ipady = 0;  GBC.weighty = 1.0; add(pInput, GBC);
        GBC.gridx = 0; GBC.gridy = 2; GBC.ipady = 20; GBC.weighty = 0.0; add(pButton, GBC);

        // Panel Header
        pHeader.setLayout(new BorderLayout(15, 0));
        pHeader.setBorder(new EmptyBorder(0, 15, 0, 15));

        lHeaderTitle.setHorizontalAlignment(JLabel.CENTER);
        pHeader.add(lHeaderTitle, BorderLayout.CENTER);

        pInput.setLayout(GBL);
        GBC.gridwidth = 1;
        GBC.weightx = 0.0; GBC.weighty = 0.0;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.ipady = 10;

        GBC.insets = new Insets(15, 40, 5, 10);
        GBC.gridx = 0; GBC.gridy = 0; pInput.add(lNombreVisitante, GBC);

        GBC.weightx = 1.0; GBC.insets = new Insets(15, 10, 5, 40);
        GBC.gridx = 1; GBC.gridy = 0; pInput.add(tfNombreVisitante, GBC);

        GBC.weightx = 0.0; GBC.insets = new Insets(5, 40, 10, 10);
        GBC.gridx = 0; GBC.gridy = 1; pInput.add(lNombreResidente, GBC);

        GBC.weightx = 1.0; GBC.insets = new Insets(5, 10, 10, 40);
        GBC.gridx = 1; GBC.gridy = 1; pInput.add(tfNombreResidente, GBC);

        pPropietarios.setLayout(new BoxLayout(pPropietarios, BoxLayout.Y_AXIS));
        pPropietarios.setBackground(Color.BLACK);

        JScrollPane spPropietarios = new JScrollPane(pPropietarios);
        spPropietarios.setBorder(BorderFactory.createLineBorder(ThemeManager.COLOR_INPUT, 1));
        spPropietarios.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spPropietarios.getViewport().setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        GBC.gridwidth = 2; GBC.weighty = 1.0;
        GBC.fill = GridBagConstraints.BOTH;
        GBC.insets = new Insets(5, 40, 15, 40);
        GBC.gridx = 0; GBC.gridy = 2; pInput.add(spPropietarios, GBC);

        pButton.setLayout(GBL);
        GBC.gridwidth = 1;
        GBC.weighty = 1.0; GBC.weightx = 0.5;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.ipady = 15;

        GBC.insets = new Insets(10, 40, 25, 10);
        GBC.gridx = 0; GBC.gridy = 0; pButton.add(bPermitirAcceso, GBC);

        GBC.insets = new Insets(10, 10, 25, 40);
        GBC.gridx = 1; GBC.gridy = 0; pButton.add(bDenegarAcceso, GBC);

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

        lNombreVisitante.setFont(ThemeManager.TEXT_SUBTITLE);
        lNombreVisitante.setForeground(ThemeManager.COLOR_TEXT);
        
        lNombreResidente.setFont(ThemeManager.TEXT_SUBTITLE);
        lNombreResidente.setForeground(ThemeManager.COLOR_TEXT);

        bPermitirAcceso.setBackground(ThemeManager.COLOR_PRIMARY);
        bPermitirAcceso.setForeground(ThemeManager.COLOR_TEXT);
        bPermitirAcceso.setFont(new Font(bPermitirAcceso.getFont().getName(),Font.BOLD,bPermitirAcceso.getFont().getSize()));

        bDenegarAcceso.setBackground(ThemeManager.COLOR_ERROR);
        bDenegarAcceso.setForeground(ThemeManager.COLOR_TEXT);
        bDenegarAcceso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bDenegarAcceso.setBackground(ThemeManager.COLOR_ERROR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bDenegarAcceso.setBackground(ThemeManager.COLOR_ERROR);
            }
        });
        bDenegarAcceso.setFont(new Font(bPermitirAcceso.getFont().getName(),Font.BOLD,bPermitirAcceso.getFont().getSize()));
    }

    private void SetEvents() {
        tfNombreResidente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                BuscarResidentes(tfNombreResidente.getText().trim());
            }
        });

        // Eventos de persistencia
        bPermitirAcceso.addActionListener(e -> RegistrarAcceso("Permitido"));
        bDenegarAcceso.addActionListener(e -> RegistrarAcceso("Denegado"));
    }

    private void BuscarResidentes(String filtro) {
        pPropietarios.removeAll();
        JPropietarios.clear();

        if (filtro.isEmpty()) {
            pPropietarios.revalidate();
            pPropietarios.repaint();
            return;
        }

        String Query = "SELECT CONCAT(R.nombre,' ',R.apellido) AS nombre_completo, numero_vivienda, calle FROM representantes as R\n" +
                        "JOIN viviendas AS V ON R.id_vivienda = V.id \n" +
                        "WHERE nombre ILIKE ? OR apellido ILIKE ? \n" +
                        "ORDER BY nombre_completo,numero_vivienda,calle ASC;";
        try {
            ResultSet RS = ConexionPostgres.consultar(Query, new Object[]{"%" + filtro + "%", "%" + filtro + "%"});
            
            while (RS != null && RS.next()) {
                String sNombre = RS.getString("nombre_completo");
                String sVivienda = RS.getString("numero_vivienda");
                String sCalle = RS.getString("calle");

                JPanel pFila = new JPanel(new BorderLayout(5,5));
                pFila.setOpaque(true);
                pFila.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
                pFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                pFila.setPreferredSize(new Dimension(0, 40));
                // Separación/margen limpio de 1px abajo de cada celda
                pFila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

                // Texto plano sin comportamiento de botón / No elegible
                JLabel lNombre = new JLabel(sNombre+" - N° Casa: "+sVivienda+" - Calle: "+sCalle);
                lNombre.setForeground(Color.WHITE);
                lNombre.setFont(ThemeManager.TEXT_NORMAL);
                // Margen interno izquierdo para separar el texto de los bordes del scroll
                lNombre.setBorder(new EmptyBorder(0, 15, 0, 0));

                pFila.add(lNombre, BorderLayout.CENTER);
                
                JPropietarios.add(pFila);
                pPropietarios.add(pFila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pPropietarios.revalidate();
        pPropietarios.repaint();
    }

    private void RegistrarAcceso(String estadoAcceso) {
        String sVisitante = tfNombreVisitante.getText().trim();

        if (sVisitante.isEmpty()) {
            ThemeManager.MostrarMensajeError(this, "Por favor, ingrese el nombre del visitante.");
            tfNombreVisitante.requestFocusInWindow();
            return;
        }

                    String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
                if (miUsuario == null) miUsuario = "Sistema_Java";


        // Registro del log directamente en la tabla de control de accesos históricos
        String QueryInsert = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; " // :C
                                       + "INSERT INTO accesos (id_carnet, tipo, fecha_hora, estado, nombre_visita) " +
                             "VALUES (NULL, 'Entrada', CURRENT_TIMESTAMP, ?, ?);";
        try {
            ConexionPostgres.comandoDML(QueryInsert, new Object[]{estadoAcceso, sVisitante});
            
            String msgExito = estadoAcceso.equals("Permitido") ? "Acceso garantizado." : "Acceso denegado.";
            ThemeManager.MostrarMensajeExito(this, msgExito);
            this.JDPadre.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fallo crítico: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}