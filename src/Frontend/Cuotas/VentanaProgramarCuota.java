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
    private final JTextField inputDescription = new JTextField();
    private final JTextField txtMonto = new JTextField();
    private final MenuCuotas menuPadre; 

    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jspHoraDesde = new JSpinner(new SpinnerDateModel());

    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jspHoraHasta = new JSpinner(new SpinnerDateModel());
    
    private final JTextFieldDateEditor DesdeEditor = (JTextFieldDateEditor) jdcDesde.getDateEditor();
    private final JTextFieldDateEditor HastaEditor = (JTextFieldDateEditor) jdcHasta.getDateEditor();

    private final Calendar calendario = Calendar.getInstance();
    private final Date currentDate = new Date(); 

    public VentanaProgramarCuota(JFrame framePadre, MenuCuotas menuPadre) {
        super(framePadre, "Programar Cuota", true); 
        this.menuPadre = menuPadre;
        
        DesdeEditor.setEditable(false);
        HastaEditor.setEditable(false);

        setUndecorated(true);
        setSize(400, 380); 
        setLocationRelativeTo(framePadre); 
        setLayout(new BorderLayout());

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(ThemeManager.COLOR_PRIMARY);
        cabecera.setPreferredSize(new Dimension(400, 40));
        
        JButton btnAtras = new JButton("←");
        btnAtras.setFont(new Font("Dialog", Font.BOLD, 16));
        btnAtras.setForeground(Color.WHITE);
        btnAtras.setBackground(ThemeManager.COLOR_PRIMARY);
        btnAtras.setFocusPainted(false);
        btnAtras.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        btnAtras.addActionListener(e -> dispose());
        cabecera.add(btnAtras, BorderLayout.WEST);

        JLabel titulo = new JLabel("Agregar Cuota", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        cabecera.add(titulo, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblDesc, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        inputDescription.setBackground(ThemeManager.COLOR_BACKGROUND);
        inputDescription.setForeground(ThemeManager.COLOR_TEXT);
        inputDescription.setCaretColor(Color.WHITE);
        cuerpo.add(inputDescription, gbc);

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblMonto = new JLabel("Monto ($):");
        lblMonto.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblMonto, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtMonto.setBackground(ThemeManager.COLOR_BACKGROUND);
        txtMonto.setForeground(ThemeManager.COLOR_TEXT);
        txtMonto.setCaretColor(Color.WHITE);
        cuerpo.add(txtMonto, gbc);

        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblFechaEmision = new JLabel("Emisión:");
        lblFechaEmision.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblFechaEmision, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(jspHoraDesde, "HH:mm:ss");
        jspHoraDesde.setEditor(editorDesde);
        
        JPanel panelFechaDesde = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFechaDesde.setOpaque(false);
        panelFechaDesde.add(jdcDesde);
        panelFechaDesde.add(jspHoraDesde);
        cuerpo.add(panelFechaDesde, gbc);
        
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel lblFechaFinal = new JLabel("Límite:");
        lblFechaFinal.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblFechaFinal, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraHasta, "HH:mm:ss");
        jspHoraHasta.setEditor(editorHasta);
        
        JPanel panelFechaHasta = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFechaHasta.setOpaque(false);
        panelFechaHasta.add(jdcHasta);
        panelFechaHasta.add(jspHoraHasta);
        cuerpo.add(panelFechaHasta, gbc);

        add(cuerpo, BorderLayout.CENTER);


        JButton btnGuardar = new JButton("Agregar Cuota");
        btnGuardar.setFont(ThemeManager.TEXT_SUBTITLE);
        btnGuardar.setForeground(ThemeManager.COLOR_TEXT);
        btnGuardar.setBackground(ThemeManager.COLOR_PRIMARY);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(400, 45));
        add(btnGuardar, BorderLayout.SOUTH);


        btnGuardar.addActionListener(e -> {
            String strDesc = inputDescription.getText().trim();
            String montoStr = txtMonto.getText().trim();

            if(strDesc.isEmpty()){
                JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN NO PUEDE ESTAR VACÍA");
                return;
            }
            if(strDesc.length() > 14){
                JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN NO PUEDE TENER MÁS DE 14 CARACTERES");
                return;
            }
            if(!strDesc.matches("^[a-zA-Z0-9ñÑ ]+$")){
                JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN DEBE SER ALFA-NUMÉRICA");
                return;
            }   
            if (!montoStr.matches("^[0-9]{1,4}+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(this, "Monto inválido, tiene que seguir el siguiente formato 'xxxx.xx'.", "Sistema Garita - ERROR X", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (existeCuota(strDesc)) {
                JOptionPane.showMessageDialog(this, 
                    "ERROR: Ya existe una cuota programada con la descripción '" + strDesc + "'.", 
                    "Cuota Duplicada", 
                    JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, 
                    "ERROR: La fecha límite de pago no puede ser anterior a la fecha de emisión.", 
                    "Error de Fechas", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";
            String queryInsert = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                               + "INSERT INTO cuotas (descripcion, monto, fecha_emision, fecha_limite, activo) VALUES (?, ?, ?, ?, ?)";

            Object[] valores = new Object[] {
                strDesc,                        
                Double.parseDouble(montoStr),   
                tsEmision,                     
                tsLimite,                       
                false                            
            };

            try {
                ConexionPostgres.comandoDML(queryInsert, valores);
                JOptionPane.showMessageDialog(this, "Cuota creada correctamente.", "Sistema Garita", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                this.menuPadre.Search(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("La inserción falló, reteniendo la ventana.");
            }
        });

        // Configuración de Spinners (solo lectura en teclado pero interactivos)
        JSpinner.DefaultEditor editorHoraDesde = (JSpinner.DefaultEditor) jspHoraDesde.getEditor();
        editorHoraDesde.getTextField().setEnabled(true);
        editorHoraDesde.getTextField().setEditable(false);

        JSpinner.DefaultEditor editorHoraHasta = (JSpinner.DefaultEditor) jspHoraHasta.getEditor();
        editorHoraHasta.getTextField().setEnabled(true);
        editorHoraHasta.getTextField().setEditable(false);

        // Inicialización y renderizado de DateChoosers según el ThemeManager
        ThemeManager.SetupDateChooser(jdcDesde);
        ThemeManager.SetupDateChooser(jdcHasta);

        jdcDesde.getDateEditor().setDate(currentDate);
        calendario.add(Calendar.MONTH, 2);
        jdcHasta.getDateEditor().setDate(calendario.getTime());
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
}