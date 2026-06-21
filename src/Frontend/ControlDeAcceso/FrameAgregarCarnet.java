package Frontend.ControlDeAcceso;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class FrameAgregarCarnet extends JPanel {

    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle = new JLabel("REGISTRAR CARNET");

    private final JPanel pInput = new JPanel();
    private final JLabel lCodigoCarnet = new JLabel("Código Carnet");
    private final JTextField tfCodigoCarnet; 
    
    private final JLabel lViviendaAsociada = new JLabel("Vivienda Asociada");
    private final JComboBox<String> cbViviendas = new JComboBox<>();

    private final JPanel pButton = new JPanel();
    private final JButton bAgregarCarnet;
    //endregion

    public FrameAgregarCarnet() {
        this.tfCodigoCarnet = TF_Custom("CRN-X99-9");
        this.bAgregarCarnet = ThemeManager.Button("Agregar Carnet");

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

        // Panel Entrada de datos
        pInput.setLayout(GBL);
        GBC.gridwidth = 1;
        GBC.weightx = 0.0;
        GBC.weighty = 0.0;
        GBC.fill = GridBagConstraints.HORIZONTAL;

        GBC.insets = new Insets(30, 40, 10, 10);
        GBC.gridx = 0; GBC.gridy = 0; pInput.add(lCodigoCarnet, GBC);

        GBC.weightx = 1.0; GBC.insets = new Insets(30, 10, 10, 40);
        GBC.gridx = 1; GBC.gridy = 0; pInput.add(tfCodigoCarnet, GBC);

        GBC.weightx = 0.0; GBC.insets = new Insets(10, 40, 30, 10);
        GBC.gridx = 0; GBC.gridy = 1; pInput.add(lViviendaAsociada, GBC);

        GBC.weightx = 1.0; GBC.insets = new Insets(10, 10, 30, 40);
        GBC.gridx = 1; GBC.gridy = 1; pInput.add(cbViviendas, GBC);

        pButton.setLayout(GBL);
        GBC.insets = new Insets(10, 40, 30, 40);
        GBC.gridx = 0; GBC.gridy = 0; GBC.weighty = 1.0; GBC.weightx = 1.0;
        pButton.add(bAgregarCarnet, GBC);

        SetTheme();
        CargarViviendas();
        SetEvents();
    }

    public void SetTheme() {
        setBackground(ThemeManager.COLOR_BACKGROUND);
        pHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pInput.setOpaque(false);
        pButton.setOpaque(false);

        lHeaderTitle.setFont(ThemeManager.TEXT_TITLE);
        lHeaderTitle.setForeground(ThemeManager.COLOR_TEXT);

        lCodigoCarnet.setFont(ThemeManager.TEXT_SUBTITLE);
        lCodigoCarnet.setForeground(ThemeManager.COLOR_TEXT);
        
        lViviendaAsociada.setFont(ThemeManager.TEXT_SUBTITLE);
        lViviendaAsociada.setForeground(ThemeManager.COLOR_TEXT);

        // Estilo ComboBox
        cbViviendas.setPreferredSize(new Dimension(250, 25));
        cbViviendas.setBackground(ThemeManager.COLOR_INPUT);
        cbViviendas.setForeground(ThemeManager.COLOR_TEXT_DARK);
        cbViviendas.setFont(ThemeManager.TEXT_NORMAL);
    }

    private void CargarViviendas() {
        cbViviendas.removeAllItems();
        cbViviendas.addItem("Seleccione una vivienda...");
        
        String query = "SELECT id, concat('Nro: ',numero_vivienda,' - Calle: ',calle) AS info FROM viviendas ORDER BY numero_vivienda,calle ASC;";
        try {
            ConexionPostgres BDD = new ConexionPostgres();
            ResultSet RS = BDD.consultar(query, null);
            while (RS != null && RS.next()) {
                cbViviendas.addItem(RS.getString("info"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void SetEvents() {
        bAgregarCarnet.addActionListener(e -> {
            String sCodigo = tfCodigoCarnet.getText().trim().toUpperCase();
            int iVivienda = cbViviendas.getSelectedIndex();

            if (sCodigo.isEmpty()) {
                ThemeManager.MostrarMensajeError(this,"Carnet vacío.");
                tfCodigoCarnet.requestFocusInWindow();
                return;
            }

            if (iVivienda <= 0) {
                ThemeManager.MostrarMensajeError(this,"Por favor, seleccione una vivienda asociada.");
                cbViviendas.requestFocusInWindow();
                return;
            }

            try {
                ConexionPostgres BDD = new ConexionPostgres();
                ResultSet RS = BDD.consultar(
                    "SELECT COUNT(*) AS total FROM carnets WHERE codigo = ? AND activo = true;", 
                    new Object[]{sCodigo});
                if (RS != null && RS.next() && RS.getInt("total") > 0) {
                    ThemeManager.MostrarMensajeError(this,"Carnet Existente.");
                    return;
                }

                String sVivienda = cbViviendas.getSelectedItem().toString();
                String sNumCasa = sVivienda.split(" - ")[0].replace("Nro: ", "").trim();
                ResultSet rsId = BDD.consultar("SELECT id FROM viviendas WHERE numero_vivienda = ? LIMIT 1;", new Object[]{sNumCasa});
                int idVivienda = -1;
                if (rsId != null && rsId.next()) {
                    idVivienda = rsId.getInt("id");
                }

                String queryInsert = "INSERT INTO carnets (codigo, id_vivienda, activo) VALUES (?, ?, true);";
                BDD.comandoDML(queryInsert, new Object[]{sCodigo, idVivienda});

                ThemeManager.MostrarMensajeExito(this,"Carnet registrado correctamente.");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    //region Helper Functions
    private JTextField TF_Custom(String placeholder) {
        JTextField TF = new JTextField("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, ThemeManager.BORDER_RADIUS_PX, ThemeManager.BORDER_RADIUS_PX); 
                g2.dispose();
                super.paintComponent(g);

                if (getText().isEmpty()) {
                    Graphics2D gPlaceholder = (Graphics2D) g.create();
                    gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gPlaceholder.setColor(ThemeManager.COLOR_PLACEHOLDER);
                    gPlaceholder.setFont(getFont());
                    
                    FontMetrics fm = gPlaceholder.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    gPlaceholder.drawString(placeholder, x, y);
                    gPlaceholder.dispose();
                }
            }
        };

        TF.setPreferredSize(new Dimension(250, 25));
        TF.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        TF.setOpaque(false);
        TF.setFont(ThemeManager.TEXT_NORMAL);
        TF.setBackground(ThemeManager.COLOR_INPUT);
        TF.setForeground(ThemeManager.COLOR_TEXT_DARK);

        TF.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) { TF.repaint(); }
            @Override public void focusLost(java.awt.event.FocusEvent evt) { TF.repaint(); }
        });
        return TF;
    }   
    //endregion
}