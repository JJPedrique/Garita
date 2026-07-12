package Frontend.Bitacora;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// =======================================================================
// COMPONENTE FILA: Tarjetas dinámicas
// =======================================================================
class JRegistroBitacora {
    JLabel NombreCompleto;
    JLabel Usuario;
    JLabel Accion;
    JLabel TablaModificada;
    JLabel FechaModificacion;

    public JRegistroBitacora(String nombre, String usuario, String accion, String tabla, String fecha) {
        this.NombreCompleto = ThemeManager.Label(nombre);
        this.Usuario = ThemeManager.Label(usuario);
        this.Accion = ThemeManager.Label(accion);
        this.TablaModificada = ThemeManager.Label(tabla);
        this.FechaModificacion = ThemeManager.Label(fecha);

        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        this.NombreCompleto.setBorder(margin);
        this.Usuario.setBorder(margin);
        this.Accion.setBorder(margin);
        this.TablaModificada.setBorder(margin);
        this.FechaModificacion.setBorder(margin);
    }

    public JPanel toPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5));
        panel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        panel.setPreferredSize(new Dimension(0, 50));

        panel.add(NombreCompleto);
        panel.add(Usuario);
        panel.add(Accion);
        panel.add(TablaModificada);
        panel.add(FechaModificacion);

        return panel;
    }
}

// =======================================================================
// INTERFAZ REESTRUCTURADA: MenuBitacora con Filtro de Nombre y Horas
// =======================================================================
public class MenuBitacora extends JPanel {
    
    // Componentes de Filtros (Barra Lateral)
    JTextField inputNombreCompleto = ThemeManager.Textfield();
    
    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jsHoraDesde = createTimeSpinner();
    
    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jsHoraHasta = createTimeSpinner();

    
    private final Calendar calendario = Calendar.getInstance();
    
    private final JTextFieldDateEditor DesdeEditor = (JTextFieldDateEditor) jdcDesde.getDateEditor();
    

    private final JTextFieldDateEditor HastaEditor = (JTextFieldDateEditor) jdcHasta.getDateEditor();
    
    JButton bBuscar = ThemeManager.Button("Buscar");

    // Contenedores dinámicos
    JPanel pTableBody = new JPanel();
    JScrollPane scrollTable;
    ArrayList<JRegistroBitacora> JRegistros = new ArrayList<>();

    public MenuBitacora() {
        this.setLayout(new BorderLayout(15, 0)); 
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. PANEL LATERAL IZQUIERDO (Filtros de Búsqueda)
        JPanel pFiltros = new JPanel(new GridBagLayout());
        pFiltros.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pFiltros.setPreferredSize(new Dimension(240, 0)); // Ampliado un poco para dar espacio a la hora
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.weightx = 1;
        gbc.gridx = 0;

        // Título del Panel de Filtros
        JLabel lTituloFiltros = ThemeManager.Label("FILTROS DE BÚSQUEDA");
        lTituloFiltros.setFont(ThemeManager.TEXT_SUBTITLE);
        lTituloFiltros.setForeground(ThemeManager.COLOR_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(5, 5, 12, 5);
        pFiltros.add(lTituloFiltros, gbc);

        // Input Nombre Completo
        JLabel lNombre = ThemeManager.Label("Nombre Completo:");
        gbc.gridy = 1; gbc.insets = new Insets(4, 5, 2, 5);
        pFiltros.add(lNombre, gbc);
        
        inputNombreCompleto.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 2; gbc.insets = new Insets(0, 5, 12, 5);
        pFiltros.add(inputNombreCompleto, gbc);
        
        // --- SECCIÓN DESDE ---
        JLabel lDesde = ThemeManager.Label("Desde Fecha:");
        gbc.gridy = 3; gbc.insets = new Insets(4, 5, 2, 5);
        pFiltros.add(lDesde, gbc);
        
        jdcDesde.setPreferredSize(new Dimension(0, 32));
        SetupDateChooser(jdcDesde);
        gbc.gridy = 4; gbc.insets = new Insets(0, 5, 4, 5);
        pFiltros.add(jdcDesde, gbc);

        JLabel lHoraDesde = ThemeManager.Label("Hora Inicial:");
        gbc.gridy = 5; gbc.insets = new Insets(2, 5, 2, 5);
        pFiltros.add(lHoraDesde, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 5, 12, 5);
        pFiltros.add(jsHoraDesde, gbc);
        
        // --- SECCIÓN HASTA ---
        JLabel lHasta = ThemeManager.Label("Hasta Fecha:");
        gbc.gridy = 7; gbc.insets = new Insets(4, 5, 2, 5);
        pFiltros.add(lHasta, gbc);
        
        jdcHasta.setPreferredSize(new Dimension(0, 32));
        SetupDateChooser(jdcHasta);
        gbc.gridy = 8; gbc.insets = new Insets(0, 5, 4, 5);
        pFiltros.add(jdcHasta, gbc);

        JLabel lHoraHasta = ThemeManager.Label("Hora Final:");
        gbc.gridy = 9; gbc.insets = new Insets(2, 5, 2, 5);
        pFiltros.add(lHoraHasta, gbc);
        gbc.gridy = 10; gbc.insets = new Insets(0, 5, 18, 5);
        pFiltros.add(jsHoraHasta, gbc);

        // Botón Buscar
        bBuscar.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 11; gbc.insets = new Insets(5, 5, 5, 5);
        pFiltros.add(bBuscar, gbc);

        // Espacio muerto al fondo
        gbc.gridy = 12; gbc.weighty = 1;
        pFiltros.add(Box.createGlue(), gbc);

        this.add(pFiltros, BorderLayout.WEST);
        
        JPanel pCentral = new JPanel(new BorderLayout());
        pCentral.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        JPanel pTableHeader = new JPanel(new GridLayout(1, 5));
        pTableHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pTableHeader.setPreferredSize(new Dimension(0, 40));

        String[] headers = {"Nombre Completo", "Usuario/Cédula", "Acción", "Tabla Afectada", "Fecha Modificación"};
        for (String h : headers) {
            JLabel lbl = ThemeManager.Label(h);
            lbl.setFont(ThemeManager.TEXT_SUBTITLE);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            pTableHeader.add(lbl);
        }
        pCentral.add(pTableHeader, BorderLayout.NORTH);

        pTableBody.setLayout(new GridBagLayout());
        pTableBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        scrollTable = new JScrollPane(pTableBody);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getVerticalScrollBar().setUnitIncrement(16);
        pCentral.add(scrollTable, BorderLayout.CENTER);

        this.add(pCentral, BorderLayout.CENTER);

        SetEvents();
        CargarBitacora();

        DesdeEditor.setEditable(false);
        JSpinner.DefaultEditor editorHoraDesde = (JSpinner.DefaultEditor)  jsHoraDesde.getEditor();
        editorHoraDesde.getTextField().setEnabled(true);
        editorHoraDesde.getTextField().setEditable(false);

        HastaEditor.setEditable(false);
        JSpinner.DefaultEditor editorHoraHasta = (JSpinner.DefaultEditor)  jsHoraHasta.getEditor();
        editorHoraHasta.getTextField().setEnabled(true);
        editorHoraHasta.getTextField().setEditable(false);

        calendario.add(Calendar.MONTH, -1);
        jdcDesde.getDateEditor().setDate(calendario.getTime());
        
        calendario.add(Calendar.MONTH, 4);
        jdcHasta.getDateEditor().setDate(calendario.getTime());
    }

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(0, 32));
        
        // Forzar fuentes consistentes
        editor.getTextField().setFont(ThemeManager.TEXT_NORMAL);
        editor.getTextField().setBackground(ThemeManager.COLOR_INPUT);
        editor.getTextField().setForeground(ThemeManager.COLOR_TEXT_DARK);
        return spinner;
    }

    private void SetupDateChooser(JDateChooser JDC) {
        JDC.setDateFormatString("yyyy-MM-dd");
        JDC.getJCalendar().getYearChooser().setFont(ThemeManager.TEXT_NORMAL);
        JDC.getJCalendar().getMonthChooser().getComboBox().setFont(ThemeManager.TEXT_NORMAL);
        JDC.getJCalendar().getDayChooser().getDayPanel().setFont(ThemeManager.TEXT_NORMAL);
    }

    private void RenderTable() {
        pTableBody.removeAll();
        GridBagConstraints GBC = new GridBagConstraints();
        GBC.anchor = GridBagConstraints.NORTH; 
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.gridwidth = 1; 
        GBC.weighty = 0;
        
        for (int i = 0; i < JRegistros.size(); i++) {
            JRegistroBitacora actual = JRegistros.get(i);
            GBC.gridx = 0; 
            GBC.gridy = i; 
            GBC.weightx = 1; 
            GBC.insets = new Insets((i == 0) ? 10 : 5, 10, 5, 10); 
            pTableBody.add(actual.toPanel(), GBC);
        }
        
        GBC.anchor = GridBagConstraints.NORTH; 
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.weightx = 1; GBC.weighty = 1;
        GBC.gridx = 0; GBC.gridy = 9999; GBC.gridwidth = 1; 
        pTableBody.add(Box.createGlue(), GBC);
        
        pTableBody.revalidate();
        pTableBody.repaint();
    }

    private void CargarBitacora() {
        JRegistros.clear();

        String QUERY = "SELECT COALESCE(concat(u.nombre,' ',u.apellido), 'Sistema') AS nombre_completo, " +
                       "b.usuario, b.accion, b.tabla_modificada, b.fecha_modificacion " +
                       "FROM bitacoras b " +
                       "LEFT JOIN usuarios u ON u.cedula = b.usuario WHERE 1=1 ";

        ArrayList<Object> params = new ArrayList<>();

        // 1. Filtrado estricto por Nombre Completo del Usuario
        String nombreBusqueda = inputNombreCompleto.getText().trim();
        if (!nombreBusqueda.isEmpty()) {
            QUERY += "AND concat(u.nombre,' ',u.apellido) ILIKE ? ";
            params.add("%" + nombreBusqueda + "%");
        }

        if (jdcDesde.getDate() != null) {
            Calendar calFecha = Calendar.getInstance();
            calFecha.setTime(jdcDesde.getDate());
            
            Calendar calHora = Calendar.getInstance();
            calHora.setTime((Date) jsHoraDesde.getValue());
            
            calFecha.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            calFecha.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            calFecha.set(Calendar.SECOND, 0);
            
            QUERY += "AND b.fecha_modificacion >= ? ";
            params.add(new java.sql.Timestamp(calFecha.getTimeInMillis()));
        }

        // 3. Combinar JDateChooser con el JSpinner de Hora (Hasta)
        if (jdcHasta.getDate() != null) {
            Calendar calFecha = Calendar.getInstance();
            calFecha.setTime(jdcHasta.getDate());
            
            Calendar calHora = Calendar.getInstance();
            calHora.setTime((Date) jsHoraHasta.getValue());
            
            calFecha.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            calFecha.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            calFecha.set(Calendar.SECOND, 59);
            
            QUERY += "AND b.fecha_modificacion <= ? ";
            params.add(new java.sql.Timestamp(calFecha.getTimeInMillis()));
        }


        QUERY += "ORDER BY b.fecha_modificacion DESC;";


        try {
            ResultSet rs = ConexionPostgres.consultar(QUERY, params.isEmpty() ? null : params.toArray());
            while (rs != null && rs.next()) {
                JRegistros.add(new JRegistroBitacora(
                    rs.getString("nombre_completo"),
                    rs.getString("usuario"),
                    rs.getString("accion"),
                    rs.getString("tabla_modificada"),
                    rs.getTimestamp("fecha_modificacion").toString()
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        RenderTable();
    }

    private void SetEvents() {
        bBuscar.addActionListener(e -> CargarBitacora());
        inputNombreCompleto.addActionListener(e -> CargarBitacora());
    }
}