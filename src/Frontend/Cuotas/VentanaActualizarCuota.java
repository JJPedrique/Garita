package Frontend.Cuotas;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import Backend.ConexionPostgres;
import Backend.ThemeManager;
import com.toedter.calendar.JDateChooser;

public class VentanaActualizarCuota extends JDialog {

    private final MenuCuotas menuPadre;
    
    // Guardamos la descripción original para usarla en el WHERE del UPDATE
    private final String descripcionOriginal;

    private final JTextField inputDescripcion = new JTextField();
    private final JTextField inputMonto = new JTextField();
    private final JDateChooser jdcLimite = new JDateChooser();
    private final JSpinner jspHoraLimite = new JSpinner(new SpinnerDateModel());

    JRadioButton radioActivo = new JRadioButton("Activo");
    JRadioButton radioInactivo = new JRadioButton("Inactivo");

    public VentanaActualizarCuota(JFrame padre, MenuCuotas menuPadre, String descripcion, String monto, String fechaLimite, String idCuota) {
        super(padre, "Actualizar Cuota", true);
        this.menuPadre = menuPadre;
        this.descripcionOriginal = descripcion;

        setUndecorated(true); // Diseño plano sin bordes nativos de Windows
        setSize(400, 350);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());

        // Cabecera de la ventana
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

        JLabel titulo = new JLabel("Modificar Cuota", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        cabecera.add(titulo, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);

        // Cuerpo del Formulario
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Descripción
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblDesc, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        inputDescripcion.setBackground(ThemeManager.COLOR_BACKGROUND);
        inputDescripcion.setForeground(ThemeManager.COLOR_TEXT);
        inputDescripcion.setCaretColor(Color.WHITE);
        cuerpo.add(inputDescripcion, gbc);

        // Fila 1: Monto
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblMonto = new JLabel("Monto ($):");
        lblMonto.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblMonto, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        inputMonto.setBackground(ThemeManager.COLOR_BACKGROUND);
        inputMonto.setForeground(ThemeManager.COLOR_TEXT);
        inputMonto.setCaretColor(Color.WHITE);
        cuerpo.add(inputMonto, gbc);

        // Fila 2: Fecha Límite
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblFecha = new JLabel("Fecha Límite:");
        lblFecha.setForeground(ThemeManager.COLOR_TEXT);
        cuerpo.add(lblFecha, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraLimite, "HH:mm:ss");
        jspHoraLimite.setEditor(editorHasta);
        
        JPanel panelFecha = new JPanel(new GridLayout(1, 2, 5, 0));
        panelFecha.setOpaque(false);
        panelFecha.add(jdcLimite);
        panelFecha.add(jspHoraLimite);
        cuerpo.add(panelFecha, gbc);

        
        //Fila 5 Activo/Inactivo
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        ButtonGroup grupo = new ButtonGroup();
        gbc.weightx = 0.5;

        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.NONE;
        
        gbc.gridx=0;gbc.gridwidth = 1;
        radioActivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        radioActivo.setForeground(ThemeManager.COLOR_TEXT);
        
        grupo.add(radioActivo);
        cuerpo.add(radioActivo, gbc);
        
        gbc.gridx=1;gbc.gridwidth = 1;
        radioInactivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        radioInactivo.setForeground(ThemeManager.COLOR_TEXT);

        grupo.add(radioInactivo);
        cuerpo.add(radioInactivo, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        
        add(cuerpo, BorderLayout.CENTER);
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(ThemeManager.COLOR_PRIMARY);
        btnGuardar.setForeground(ThemeManager.COLOR_TEXT);
        btnGuardar.setFont(ThemeManager.TEXT_SUBTITLE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(400, 45));
        btnGuardar.addActionListener(e -> actualizarCuota(idCuota));
        add(btnGuardar, BorderLayout.SOUTH);

        inputDescripcion.setText(descripcion);
        inputMonto.setText(monto);
        try {
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaLimite);
            jdcLimite.setDate(date);
            jspHoraLimite.setValue(date);
        } catch (Exception e) {
            jdcLimite.setDate(new java.util.Date());
        }
    }

    private void actualizarCuota(String idCuota) {
        String nuevaDesc = inputDescripcion.getText().trim();
        String nuevoMonto = inputMonto.getText().trim();
        Boolean nuevoEstado = true;
        

        // Validaciones idénticas a tus expresiones regulares de filtrado
        if (!nuevaDesc.matches("^[a-zA-Z0-9ñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "LA DESCRIPCIÓN DEBE SER ALFA-NUMÉRICA");
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

        if (!radioActivo.isSelected() && !radioInactivo.isSelected()  ){
            JOptionPane.showMessageDialog(this, "DEBE SELECCIONAR UN ESTADO");
            return;
        }

        if (radioActivo.isSelected()) {
            nuevoEstado = true;
        }

        if (radioInactivo.isSelected()) {
            nuevoEstado = false;
        }

        // Construir la Fecha Completa con la Hora
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

        try {
            String queryUpdate = "UPDATE cuotas SET descripcion = ?, monto = ?::numeric, fecha_limite = ?::timestamp, activo = ? WHERE id = ?::integer";
            Object[] valores = {nuevaDesc, Double.parseDouble(nuevoMonto), java.sql.Timestamp.valueOf(strFecha), nuevoEstado,idCuota};
            System.out.println(idCuota);
            ConexionPostgres.comandoDML(queryUpdate, valores);
            
            JOptionPane.showMessageDialog(this, "Cuota actualizada correctamente.", "Sistema Garita", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            this.menuPadre.Search(); // Refresca la JTable del menú principal
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}