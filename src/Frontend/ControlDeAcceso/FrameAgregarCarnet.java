package Frontend.ControlDeAcceso;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    
    private JDialog JDPadre;
    private Runnable onActualizarTabla;
    //endregion

    public FrameAgregarCarnet(JDialog JDPadre, Runnable onActualizarTabla) {
        this.JDPadre = JDPadre;
        this.onActualizarTabla = onActualizarTabla;
        this.tfCodigoCarnet = ThemeManager.Textfield();
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
        GBC.ipady = 10;

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
<<<<<<< Updated upstream
            ResultSet RS_Carnet = MenuControlDeAcceso.BDD.consultar(query, null);
            while (RS_Carnet != null && RS_Carnet.next()) {
                cbViviendas.addItem(RS_Carnet.getString("info"));
=======
            
            ResultSet RS = ConexionPostgres.consultar(query, null);
            while (RS != null && RS.next()) {
                cbViviendas.addItem(RS.getString("info"));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        
                String sVivienda = cbViviendas.getSelectedItem().toString();
                String sNumCasa = sVivienda.split(" - ")[0].replace("Nro: ", "").trim();
                
                ResultSet RS_Vivienda = MenuControlDeAcceso.BDD.consultar(
                    "SELECT id FROM viviendas WHERE numero_vivienda = ? LIMIT 1;", 
                    new Object[]{sNumCasa}
                );
        
                int idVivienda = -1;
                if (RS_Vivienda != null && RS_Vivienda.next()) {
                    idVivienda = RS_Vivienda.getInt("id");
                }
                
                ResultSet RS_Carnet = MenuControlDeAcceso.BDD.consultar(
                    "SELECT id,activo FROM carnets WHERE codigo = ? LIMIT 1;", 
                    new Object[]{sCodigo}
                );

                int idCarnet = -1;
                boolean activo = true;
                if (RS_Carnet != null && RS_Carnet.next()) {
                    idCarnet = RS_Carnet.getInt("id");
                    activo = RS_Carnet.getBoolean("activo");
                }

                if(idCarnet != -1 && activo == false){
                    MenuControlDeAcceso.BDD.comandoDML("UPDATE carnets SET codigo = ?, id_vivienda = ?, activo = true WHERE id = ?;", 
                    new Object[]{sCodigo, idVivienda, idCarnet});
                    
                    ThemeManager.MostrarMensajeExito(this,"Carnet registrado correctamente.");
                    TriggerActualizarTabla();
                    this.JDPadre.dispose();
                    return;
                }
                if(idCarnet != -1 && activo == true){
                    ThemeManager.MostrarMensajeError(this,"El carnet ya existe.");
                    return;
                }

                String queryInsert = "INSERT INTO carnets (codigo, id_vivienda, activo) VALUES (?, ?, true);";
                MenuControlDeAcceso.BDD.comandoDML(queryInsert, new Object[]{sCodigo, idVivienda});
=======
                ResultSet RS = ConexionPostgres.consultar(
                    "SELECT COUNT(*) AS total FROM carnets WHERE codigo = ? AND activo = true;", 
                    new Object[]{sCodigo});
                if (RS != null && RS.next() && RS.getInt("total") > 0) {
                    ThemeManager.MostrarMensajeError(this,"Carnet Existente.");
                    return;
                }

                String sVivienda = cbViviendas.getSelectedItem().toString();
                String sNumCasa = sVivienda.split(" - ")[0].replace("Nro: ", "").trim();
                ResultSet rsId = ConexionPostgres.consultar("SELECT id FROM viviendas WHERE numero_vivienda = ? LIMIT 1;", new Object[]{sNumCasa});
                int idVivienda = -1;
                if (rsId != null && rsId.next()) {
                    idVivienda = rsId.getInt("id");
                }

                String queryInsert = "INSERT INTO carnets (codigo, id_vivienda, activo) VALUES (?, ?, true);";
                ConexionPostgres.comandoDML(queryInsert, new Object[]{sCodigo, idVivienda});
>>>>>>> Stashed changes

                ThemeManager.MostrarMensajeExito(this,"Carnet registrado correctamente.");
                TriggerActualizarTabla();
                this.JDPadre.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void TriggerActualizarTabla(){
        if(onActualizarTabla != null){
            onActualizarTabla.run();
        }
    }
}