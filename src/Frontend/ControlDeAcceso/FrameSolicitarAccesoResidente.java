package Frontend.ControlDeAcceso;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import Backend.ThemeManager.LimiteCaracteresFilter;

public class FrameSolicitarAccesoResidente extends JPanel {

    //region Componentes
    private final GridBagLayout GBL = new GridBagLayout();
    private final GridBagConstraints GBC = new GridBagConstraints();

    private final JPanel pHeader = new JPanel();
    private final JLabel lHeaderTitle = new JLabel("SOLICITAR ACCESO RESIDENTE");

    private final JPanel pInput = new JPanel();
    private final JLabel lCodigoCarnet = ThemeManager.Label("Código de carnet");
    private final JTextField tfCodigoCarnet = ThemeManager.Textfield("0000000000");
    
    private final JPanel pTipoAcceso = new JPanel();
    private final JLabel lTipoAcceso = ThemeManager.Label("Tipo de Acceso");
    private final JRadioButton rbEntrada = new JRadioButton("Entrada",true);
    private final JRadioButton rbSalida = new JRadioButton("Salida");

    private final JPanel pButton = new JPanel();
    private final JButton bAcceder = ThemeManager.Button("Acceder");

    private JDialog JDPadre;
    //endregion

    public FrameSolicitarAccesoResidente(JDialog JDPadre) {
        this.JDPadre = JDPadre;

        AbstractDocument AD;
        AD = (AbstractDocument) tfCodigoCarnet.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(10));

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

        GBC.insets = new Insets(15, 40, 5, 40);
        lCodigoCarnet.setHorizontalAlignment(JLabel.CENTER);
        GBC.gridx = 0; GBC.gridy = 0; pInput.add(lCodigoCarnet, GBC);

        GBC.weightx = 1.0; GBC.insets = new Insets(15, 40, 5, 40);
        GBC.gridx = 0; GBC.gridy = 1; pInput.add(tfCodigoCarnet, GBC);

        GBC.insets = new Insets(15, 40, 5, 40);
        GBC.gridx = 0; GBC.gridy = 2; pInput.add(pTipoAcceso, GBC);

        pTipoAcceso.setLayout(GBL);
        GBC.insets = new Insets(2, 2, 2, 2);
        GBC.gridwidth = 1;
        lTipoAcceso.setHorizontalAlignment(JLabel.CENTER);
        GBC.gridx = 0; GBC.gridy = 0; pTipoAcceso.add(lTipoAcceso, GBC); 

        ButtonGroup BG = new ButtonGroup();
        BG.add(rbEntrada);
        BG.add(rbSalida);

        JPanel pBotonesAcceso = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pBotonesAcceso.setOpaque(false);
        pBotonesAcceso.add(rbEntrada);
        pBotonesAcceso.add(rbSalida);

        GBC.gridx = 0; GBC.gridy = 1; 
        pTipoAcceso.add(pBotonesAcceso, GBC); 
    

        pButton.setLayout(GBL);
        GBC.gridwidth = 1;
        GBC.weighty = 1.0; GBC.weightx = 0.5;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.ipady = 15;

        GBC.insets = new Insets(10, 40, 25, 40);
        GBC.gridx = 0; GBC.gridy = 0; pButton.add(bAcceder, GBC);

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

        lCodigoCarnet.setFont(ThemeManager.TEXT_SUBTITLE);
        lCodigoCarnet.setForeground(ThemeManager.COLOR_TEXT);

        pTipoAcceso.setBackground(ThemeManager.COLOR_BACKGROUND);

        rbEntrada.setOpaque(false);
        rbSalida.setOpaque(false);

        lTipoAcceso.setFont(ThemeManager.TEXT_SUBTITLE);
        rbEntrada.setFont(ThemeManager.TEXT_NORMAL);
        rbSalida.setFont(ThemeManager.TEXT_NORMAL);
        lTipoAcceso.setForeground(ThemeManager.COLOR_TEXT);
        rbEntrada.setForeground(ThemeManager.COLOR_TEXT);
        rbSalida.setForeground(ThemeManager.COLOR_TEXT);

        bAcceder.setBackground(ThemeManager.COLOR_PRIMARY);
        bAcceder.setForeground(ThemeManager.COLOR_TEXT);
        bAcceder.setFont(new Font(bAcceder.getFont().getName(),Font.BOLD,bAcceder.getFont().getSize()));
    }

    private void SetEvents() {
        bAcceder.addActionListener(e -> {
            String Query = "SELECT  \n" +
                            "    CU.descripcion AS cuota_pendiente, \n" +
                            "    CU.monto AS monto_deuda, \n" + 
                            "    CURRENT_DATE - CU.fecha_limite::date AS dias_retraso \n" + 
                            "\n" + 
                            "FROM carnets CA \n" + 
                            "JOIN viviendas V ON CA.id_vivienda = V.id \n" + 
                            "CROSS JOIN cuotas CU \n" + 
                            "\n" + 
                            "WHERE CA.codigo = ?\n" + 
                            "  AND CU.activo = true \n" + 
                            "  AND CU.fecha_limite < CURRENT_TIMESTAMP \n" + 
                            "  AND CU.fecha_emision >= V.fecha_registro\n" + 
                            "  \n" + 
                            "  AND NOT EXISTS (\n" + 
                            "      SELECT 1 \n" + 
                            "      FROM pagos_realizados PR \n" + 
                            "      WHERE PR.id_vivienda = V.id AND PR.id_cuota = CU.id\n" + 
                            "  ) \n" + 
                            "ORDER BY CU.fecha_limite ASC;";

            String Query_Carnet = "SELECT id FROM carnets WHERE codigo = ? LIMIT 1";

            String sCodigoCarnet = tfCodigoCarnet.getText().trim().toUpperCase();
            if(sCodigoCarnet.isEmpty()){
                ThemeManager.MostrarMensajeError(this,"Código de carnet vacío.");
                return;
            }

            String sTipoAcceso = rbEntrada.isSelected() ? "Entrada" : "Salida";

            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";

            try {
                ResultSet RS_Carnet = ConexionPostgres.consultar(Query_Carnet, new Object[]{sCodigoCarnet});

                int idCarnet = -1;
                while (RS_Carnet != null && RS_Carnet.next()) {
                    idCarnet = RS_Carnet.getInt("id");
                }

                if(idCarnet == -1){
                    ThemeManager.MostrarMensajeError(this, "El código de carnet no existe.");
                    return;
                }

                ResultSet RS = ConexionPostgres.consultar(Query,new Object[]{sCodigoCarnet});

                ArrayList<Object[]> TUPLAS = new ArrayList<>();

                while (RS != null && RS.next()) {
                    String sDesCuota = RS.getString("cuota_pendiente");
                    double sMonto = RS.getDouble("monto_deuda");
                    int sRetraso = RS.getInt("dias_retraso");
                    TUPLAS.add(new Object[]{sDesCuota,sMonto,sRetraso});
                }

                if(TUPLAS.isEmpty()){
                    ThemeManager.MostrarMensajeExito(this, "Acceso permitido.");
                    InsertBDD(miUsuario,idCarnet,sTipoAcceso,"Permitido");
                    this.JDPadre.dispose();
                    return;
                }

                String sMoroso = "Acceso denegado. El residente no está solvente.";
                
                ThemeManager.MostrarMensajeError(this, sMoroso);
                InsertBDD(miUsuario,idCarnet,sTipoAcceso,"Denegado");
                this.JDPadre.dispose();
                return;

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void InsertBDD (String usuario, int codigo, String tipoAcceso, String estado) throws SQLException{
        ConexionPostgres.comandoDML(
            "DO $$ BEGIN PERFORM set_config('app.usuario_actual',\'"+usuario+"\', true); END $$; " // :C
            + "INSERT INTO accesos (id_carnet, tipo, fecha_hora, estado, nombre_visita) " +
            "VALUES (?, ?, CURRENT_TIMESTAMP, ?, NULL);", new Object[]{codigo,tipoAcceso,estado});
    }
}