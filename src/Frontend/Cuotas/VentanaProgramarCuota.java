package Frontend.Cuotas;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class VentanaProgramarCuota extends JDialog {
    // Campos para la descripción con formato "Cuota ENE 2026"
    private final JComboBox<String> cmbMes = new JComboBox<>(new String[]{"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"});
    private final JTextField txtAño = ThemeManager.Textfield();
    private final JTextField txtMonto = ThemeManager.Textfield("15.0");
    private final MenuCuotas menuPadre; 

    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jspHoraDesde = new JSpinner(new SpinnerDateModel());

    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jspHoraHasta = new JSpinner(new SpinnerDateModel());
    
    private final JTextFieldDateEditor DesdeEditor = (JTextFieldDateEditor) jdcDesde.getDateEditor();
    private final JTextFieldDateEditor HastaEditor = (JTextFieldDateEditor) jdcHasta.getDateEditor();

    private final Calendar calendario = Calendar.getInstance();
    private final Date currentDate = new Date(); 

    JPanel cabecera = new JPanel(new BorderLayout());
    
    JLabel titulo = new JLabel("Programar Cuota", SwingConstants.CENTER);
    JPanel cuerpo = new JPanel(new GridBagLayout());

    JLabel lblDesc = new JLabel("Descripción Cuota:");
    JLabel lblMonto = new JLabel("Monto ($):");
    JLabel lblFechaEmision = new JLabel("Emisión:");
    JLabel lblFechaFinal = new JLabel("Límite:");

    JButton btnGuardar = ThemeManager.Button("Programar Cuota");


    public VentanaProgramarCuota(JFrame framePadre, MenuCuotas menuPadre) {
        super(framePadre, "Programar Cuota", true); 
        this.menuPadre = menuPadre;
        
        DesdeEditor.setEditable(false);
        HastaEditor.setEditable(false);

        // Configurar el campo de año para solo números
        txtAño.setColumns(4);
        txtAño.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                if (str == null) return;
                if (!str.matches("\\d*")) return;
                if (getLength() + str.length() > 4) return;
                super.insertString(offs, str, a);
            }
        });

        setSize(500, 420); 
        setLocationRelativeTo(framePadre); 
        setLayout(new BorderLayout());

        cabecera.setBackground(ThemeManager.COLOR_PRIMARY);
        cabecera.setPreferredSize(new Dimension(500, 40));

        titulo.setForeground(Color.WHITE);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        cabecera.add(titulo, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Descripción (Mes + Año)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        cuerpo.add(lblDesc, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JPanel panelDescripcion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelDescripcion.setOpaque(false);
        

        
        panelDescripcion.add(cmbMes);
        panelDescripcion.add(txtAño);
        cuerpo.add(panelDescripcion, gbc);

        // Fila 1: Monto
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        cuerpo.add(lblMonto, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        cuerpo.add(txtMonto, gbc);

        // Fila 2: Fecha Emisión
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        cuerpo.add(lblFechaEmision, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(jspHoraDesde, "HH:mm:ss");
        jspHoraDesde.setEditor(editorDesde);
        
        JPanel panelFechaDesde = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFechaDesde.setOpaque(false);
        panelFechaDesde.add(jdcDesde);
        panelFechaDesde.add(jspHoraDesde);
        cuerpo.add(panelFechaDesde, gbc);
        
        // Fila 3: Fecha Límite
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        cuerpo.add(lblFechaFinal, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraHasta, "HH:mm:ss");
        jspHoraHasta.setEditor(editorHasta);
        
        JPanel panelFechaHasta = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFechaHasta.setOpaque(false);
        panelFechaHasta.add(jdcHasta);
        panelFechaHasta.add(jspHoraHasta);
        cuerpo.add(panelFechaHasta, gbc);

        gbc.insets = new Insets(20, 12, 8, 12);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weighty = 0; gbc.weightx = 0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;

        cuerpo.add(btnGuardar, gbc);
        
        add(cuerpo, BorderLayout.CENTER);

        // Establecer año por defecto
        txtAño.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        btnGuardar.addActionListener(e -> {
            String mes = (String) cmbMes.getSelectedItem();
            String año = txtAño.getText().trim();
            String strDesc = "Cuota " + mes + " " + año;
            String montoStr = txtMonto.getText().trim();

            if (año.isEmpty() || año.length() != 4) {
                ThemeManager.MostrarMensajeError(this, "DEBE INGRESAR UN AÑO VÁLIDO (4 dígitos)");
                return;
            }

            if (!montoStr.matches("^[0-9]{1,4}+(\\.[0-9]{1,2})?$")) {
                ThemeManager.MostrarMensajeError(this, "Monto inválido, tiene que seguir el siguiente formato 'xxxx.xx'.");
                return;
            }
            if (existeCuota(strDesc)) {
                ThemeManager.MostrarMensajeError(this, 
                    "ERROR: Ya existe una cuota programada con la descripción '" + strDesc + "'.");
                return; 
            }

            // Procesar la fecha y hora de emisión
            java.util.Calendar calEmision = java.util.Calendar.getInstance();
            calEmision.setTime(jdcDesde.getDate());
            java.util.Calendar horaEmision = java.util.Calendar.getInstance();
            horaEmision.setTime((java.util.Date) jspHoraDesde.getValue());
            calEmision.set(java.util.Calendar.HOUR_OF_DAY, horaEmision.get(java.util.Calendar.HOUR_OF_DAY));
            calEmision.set(java.util.Calendar.MINUTE, horaEmision.get(java.util.Calendar.MINUTE));
            calEmision.set(java.util.Calendar.SECOND, horaEmision.get(java.util.Calendar.SECOND));
            java.sql.Timestamp tsEmision = new java.sql.Timestamp(calEmision.getTimeInMillis());

            // Procesar la fecha y hora límite
            java.util.Calendar calLimite = java.util.Calendar.getInstance();
            calLimite.setTime(jdcHasta.getDate());
            java.util.Calendar horaLimite = java.util.Calendar.getInstance();
            horaLimite.setTime((java.util.Date) jspHoraHasta.getValue());
            calLimite.set(java.util.Calendar.HOUR_OF_DAY, horaLimite.get(java.util.Calendar.HOUR_OF_DAY));
            calLimite.set(java.util.Calendar.MINUTE, horaLimite.get(java.util.Calendar.MINUTE));
            calLimite.set(java.util.Calendar.SECOND, horaLimite.get(java.util.Calendar.SECOND));
            java.sql.Timestamp tsLimite = new java.sql.Timestamp(calLimite.getTimeInMillis());

            if(tsLimite.before(tsEmision)){
                ThemeManager.MostrarMensajeError(this, 
                    "La fecha límite de pago no puede ser anterior a la fecha de emisión.");
                return;
            }

            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";
            String queryInsert = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                               + "INSERT INTO cuotas (descripcion, monto, fecha_emision, fecha_limite, activo, borrada) VALUES (?, ?, ?, ?, ?, ?)";

            Object[] valores = new Object[] {
                strDesc,                        
                Double.parseDouble(montoStr),   
                tsEmision,                     
                tsLimite,                       
                false,
                false                          
            };

            try {
                ConexionPostgres.comandoDML(queryInsert, valores);
                ThemeManager.MostrarMensajeExito(this, "Cuota creada correctamente.");
                dispose();
                this.menuPadre.Search(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("La inserción falló, reteniendo la ventana.");
            }
        });

        // Configuración de Spinners
        JSpinner.DefaultEditor editorHoraDesde = (JSpinner.DefaultEditor) jspHoraDesde.getEditor();
        editorHoraDesde.getTextField().setEnabled(true);
        editorHoraDesde.getTextField().setEditable(false);

        JSpinner.DefaultEditor editorHoraHasta = (JSpinner.DefaultEditor) jspHoraHasta.getEditor();
        editorHoraHasta.getTextField().setEnabled(true);
        editorHoraHasta.getTextField().setEditable(false);

        ThemeManager.SetupDateChooser(jdcDesde);
        ThemeManager.SetupDateChooser(jdcHasta);
        ThemeManager.SetupTimeSpinnerCuota(jspHoraDesde);
        ThemeManager.SetupTimeSpinnerCuota(jspHoraHasta);

        jdcDesde.getDateEditor().setDate(currentDate);
        calendario.add(Calendar.MONTH, 2);
        jdcHasta.getDateEditor().setDate(calendario.getTime());
        SetTheme();
    }

    private boolean existeCuota(String descripcion) {
        String query = "SELECT COUNT(*) AS total FROM cuotas WHERE UPPER(TRIM(descripcion)) = UPPER(TRIM(?))";
        Object[] params = new Object[] { descripcion };
        try {
            java.sql.ResultSet rs = ConexionPostgres.consultar(query, params);
            if (rs != null && rs.next()) {
                return rs.getInt("total") > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al verificar la duplicidad de la cuota.");
        }
        return false;
    }

    public void SetTheme() {
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND);
        cabecera.setBackground(ThemeManager.COLOR_PRIMARY);

        titulo.setFont(ThemeManager.TEXT_TITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);

        // Estilos para el combo y campo de año
        /*
        cmbMes.setBackground(ThemeManager.COLOR_SECONDARY);
        cmbMes.setForeground(ThemeManager.COLOR_TEXT);
        cmbMes.setFont(ThemeManager.TEXT_SUBTITLE);
        
        txtAño.setBackground(ThemeManager.COLOR_SECONDARY);
        txtAño.setForeground(ThemeManager.COLOR_TEXT);
        txtAño.setFont(ThemeManager.TEXT_SUBTITLE);
        */
        

        lblFechaEmision.setForeground(ThemeManager.COLOR_TEXT);
        lblFechaEmision.setFont(ThemeManager.TEXT_SUBTITLE);
        
        lblMonto.setForeground(ThemeManager.COLOR_TEXT);
        lblMonto.setFont(ThemeManager.TEXT_SUBTITLE);
        
        lblDesc.setForeground(ThemeManager.COLOR_TEXT);
        lblDesc.setFont(ThemeManager.TEXT_SUBTITLE);
        
        lblFechaFinal.setForeground(ThemeManager.COLOR_TEXT);
        lblFechaFinal.setFont(ThemeManager.TEXT_SUBTITLE);
    }
    
}