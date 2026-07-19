package Frontend.Cuotas;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import Backend.ConexionPostgres;
import Backend.ThemeManager;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

public class VentanaActualizarCuota extends JDialog {

    private final MenuCuotas menuPadre;

    // Campos para la descripción con formato "Descripción Cuota ENE  2026"
    private final JComboBox<String> cmbMes = new JComboBox<>(new String[]{"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"});
    private final JTextField txtAño = ThemeManager.Textfield();
    private final JTextField txtMonto = ThemeManager.Textfield();
    private final JDateChooser jdcLimite = new JDateChooser();
    private final JSpinner jspHoraLimite = new JSpinner(new SpinnerDateModel());

    private final JTextFieldDateEditor LimiteEditor = (JTextFieldDateEditor) jdcLimite.getDateEditor();

    JRadioButton radioActivo = new JRadioButton("Activo");
    JRadioButton radioInactivo = new JRadioButton("Inactivo");

    JPanel cabecera = new JPanel(new BorderLayout());
    JButton btnAtras = new JButton("←");
    JLabel titulo = new JLabel("Modificar Cuota", SwingConstants.CENTER);
    JPanel cuerpo = new JPanel(new GridBagLayout());
    
    JLabel lblDesc = new JLabel("Descripción Cuota:");
    JLabel lblMonto = new JLabel("Monto ($):");
    JLabel lblFecha = new JLabel("Fecha Límite:");
    JButton btnGuardar = ThemeManager.Button("Actualizar Cuota");

    public VentanaActualizarCuota(JFrame padre, MenuCuotas menuPadre, String descripcion, String monto, String fechaLimite, String idCuota) {
        
        super(padre, "Actualizar Cuota", true);
        this.menuPadre = menuPadre;
        
        LimiteEditor.setEditable(false);
        
        setSize(500, 420); 
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());

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

        cabecera.setPreferredSize(new Dimension(500, 40));
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

        // Fila 2: Fecha Límite
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        cuerpo.add(lblFecha, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraLimite, "HH:mm:ss");
        jspHoraLimite.setEditor(editorHasta);
        
        JPanel panelFecha = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFecha.setOpaque(false);
        panelFecha.add(jdcLimite);
        panelFecha.add(jspHoraLimite);
        cuerpo.add(panelFecha, gbc);

        // Fila 3: Activo/Inactivo
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        ButtonGroup grupo = new ButtonGroup();
        gbc.weightx = 0.5;

        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.NONE;
        
        gbc.gridx = 0; gbc.gridwidth = 1;
        grupo.add(radioActivo);
        cuerpo.add(radioActivo, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 1;
        grupo.add(radioInactivo);
        cuerpo.add(radioInactivo, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.insets = new Insets(20, 12, 8, 12);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weighty = 0; gbc.weightx = 1.0;
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(500, 45));
        btnGuardar.addActionListener(e -> actualizarCuota(idCuota));
        cuerpo.add(btnGuardar, gbc);
        
        add(cuerpo, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarDatosIniciales(descripcion, monto, fechaLimite);
        
        ThemeManager.SetupDateChooser(jdcLimite);

        SetTheme();
    }

    private void cargarDatosIniciales(String descripcion, String monto, String fechaLimite) {
        // Parsear la descripción en formato "Descripción Cuota ENE 2026"
        String[] partes = descripcion.split(" ");
        if (partes.length >= 3) {
            String mes = partes[partes.length - 2]; // ENE
            String año = partes[partes.length - 1]; // 2026
            
            // Seleccionar el mes en el combo
            for (int i = 0; i < cmbMes.getItemCount(); i++) {
                if (cmbMes.getItemAt(i).equals(mes)) {
                    cmbMes.setSelectedIndex(i);
                    break;
                }
            }
            txtAño.setText(año);
        }
        
        txtMonto.setText(monto);
        try {
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaLimite);
            jdcLimite.setDate(date);
            jspHoraLimite.setValue(date);
        } catch (Exception e) {
            jdcLimite.setDate(new java.util.Date());
        }
        
        JSpinner.DefaultEditor editorHoraDesde = (JSpinner.DefaultEditor) jspHoraLimite.getEditor();
        editorHoraDesde.getTextField().setEnabled(true);
        editorHoraDesde.getTextField().setEditable(false);
    }

    private String construirDescripcion() {
        String mes = (String) cmbMes.getSelectedItem();
        String año = txtAño.getText().trim();
        return "Cuota " + mes + " " + año;
    }

    private void actualizarCuota(String idCuota) {
        String nuevaDesc = construirDescripcion();
        String nuevoMonto = txtMonto.getText().trim();
        Boolean nuevoEstado = true;

        if (txtAño.getText().trim().isEmpty() || txtAño.getText().trim().length() != 4) {
            JOptionPane.showMessageDialog(this, "DEBE INGRESAR UN AÑO VÁLIDO (4 dígitos)");
            return;
        }

        if (!nuevoMonto.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            JOptionPane.showMessageDialog(this, "EL MONTO DEBE SER NUMÉRICO (EJ: 10 o 12.50)");
            return;
        }
        if (jdcLimite.getDate() == null) {
            JOptionPane.showMessageDialog(this, "DEBE SELECCIONAR UNA FECHA LÍMITE");
            return;
        }
        if (!radioActivo.isSelected() && !radioInactivo.isSelected()) {
            JOptionPane.showMessageDialog(this, "DEBE SELECCIONAR UN ESTADO");
            return;
        }

        if (radioActivo.isSelected()) nuevoEstado = true;
        if (radioInactivo.isSelected()) nuevoEstado = false;

        if (existeCuotaDuplicada(nuevaDesc, idCuota)) {
            JOptionPane.showMessageDialog(this, 
                "ERROR: Ya existe otra cuota registrada con la descripción '" + nuevaDesc + "'.", 
                "Cuota Duplicada", 
                JOptionPane.ERROR_MESSAGE);
            return; 
        }

        java.util.Date fecha = jdcLimite.getDate();
        java.util.Date hora = (java.util.Date) jspHoraLimite.getValue();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fecha);
        java.util.Calendar calHora = java.util.Calendar.getInstance();
        calHora.setTime(hora);
        cal.set(java.util.Calendar.HOUR_OF_DAY, calHora.get(java.util.Calendar.HOUR_OF_DAY));
        cal.set(java.util.Calendar.MINUTE, calHora.get(java.util.Calendar.MINUTE));
        cal.set(java.util.Calendar.SECOND, calHora.get(java.util.Calendar.SECOND));

        String strFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        java.sql.Timestamp tsNuevaLimite = java.sql.Timestamp.valueOf(strFecha);

        try {
            String queryCheck = "Select fecha_emision From cuotas WHERE id = ?::integer";
            java.sql.ResultSet rs = ConexionPostgres.consultar(queryCheck, new Object[]{idCuota});

            if (rs != null && rs.next()) {
                java.sql.Timestamp tsEmisionActual = rs.getTimestamp("fecha_emision");
                
                if (tsNuevaLimite.before(tsEmisionActual)) {
                    JOptionPane.showMessageDialog(this, 
                        "ERROR: La nueva fecha límite no puede ser anterior a la fecha de emisión original (" + tsEmisionActual + ").", 
                        "Error de Fechas", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";
            String queryUpdate = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                               + "UPDATE cuotas SET descripcion = ?, monto = ?::numeric, fecha_limite = ?::timestamp, activo = ? WHERE id = ?::integer";

            Object[] valores = { nuevaDesc, Double.parseDouble(nuevoMonto), java.sql.Timestamp.valueOf(strFecha), nuevoEstado, idCuota };

            ConexionPostgres.comandoDML(queryUpdate, valores);
            
            JOptionPane.showMessageDialog(this, "Cuota actualizada correctamente.", "Sistema Garita", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            this.menuPadre.Search(); 
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean existeCuotaDuplicada(String descripcion, String idCuota) {
        String query = "SELECT COUNT(*) AS total FROM cuotas WHERE UPPER(TRIM(descripcion)) = UPPER(TRIM(?)) AND id != ?::integer";
        Object[] params = new Object[] { descripcion, idCuota };
        
        try {
            ResultSet rs = ConexionPostgres.consultar(query, params);
            if (rs != null && rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al comprobar duplicados en actualización.");
        }
        return false;
    }

    public void SetTheme() {
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND);
        cabecera.setBackground(ThemeManager.COLOR_PRIMARY);
        btnAtras.setBackground(ThemeManager.COLOR_PRIMARY);

        titulo.setFont(ThemeManager.TEXT_TITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);

        btnGuardar.setBackground(ThemeManager.COLOR_PRIMARY);
        btnGuardar.setForeground(ThemeManager.COLOR_TEXT);
        btnGuardar.setFont(ThemeManager.TEXT_SUBTITLE);

        JLabel[] labels = {lblDesc, lblMonto, lblFecha};
        for (JLabel label : labels) {
            label.setForeground(ThemeManager.COLOR_TEXT);
            label.setFont(ThemeManager.TEXT_SUBTITLE);
        }



        radioActivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        radioActivo.setForeground(ThemeManager.COLOR_TEXT);
        radioActivo.setFont(ThemeManager.TEXT_SUBTITLE);

        radioInactivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        radioInactivo.setForeground(ThemeManager.COLOR_TEXT);
        radioInactivo.setFont(ThemeManager.TEXT_SUBTITLE);
    }
}