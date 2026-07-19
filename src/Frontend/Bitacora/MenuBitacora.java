package Frontend.Bitacora;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

import com.toedter.calendar.JDateChooser;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import Backend.ThemeManager.LimiteCaracteresFilter;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

// =======================================================================
// COMPONENTE TARJETA: Similar a JRegistroAcceso
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
        JPanel ROW = new JPanel(new GridLayout(1, 5));
        ROW.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        ROW.setPreferredSize(new Dimension(0, 45));
        ROW.setMinimumSize(new Dimension(0, 45));
        ROW.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        ROW.add(NombreCompleto);
        ROW.add(Usuario);
        ROW.add(Accion);
        ROW.add(TablaModificada);
        ROW.add(FechaModificacion);
        
        return ROW;
    }
}

// =======================================================================
// MENÚ PRINCIPAL - BASADO EN MenuRegistroDeAcceso
// =======================================================================
public class MenuBitacora extends JPanel {

    //region Componentes
    GridBagLayout GBL = new GridBagLayout();
    GridBagConstraints GBC = new GridBagConstraints();

    JPanel pFunctions = new JPanel();
    JPanel pTabla = new JPanel();
    JPanel pTablaHeader = new JPanel();
    JPanel pTablaBody = new JPanel();

    JLabel lBusquedaFiltro = ThemeManager.Label("BÚSQUEDA Y FILTROS");
    
    JLabel lNombreCompleto = ThemeManager.Label("Nombre Completo");
    JLabel lUsuario = ThemeManager.Label("Usuario/Cédula");
    JLabel lAccion = ThemeManager.Label("Acción");




    JLabel lTabla = ThemeManager.Label("Tabla Afectada");

    JTextField tfNombreCompleto = ThemeManager.Textfield("Roberto Montero");
    JTextField tfUsuario = ThemeManager.Textfield("v-88888888");

    // Radio Buttons para Estado
    JRadioButton rbTodos = new JRadioButton("Todos", true);
    JRadioButton rbInsert = new JRadioButton("Insert");
    JRadioButton rbUpdate = new JRadioButton("Update");
    JRadioButton rbDelete = new JRadioButton("Delete");
    ButtonGroup bgAccion = new ButtonGroup();
    JCheckBox cbTime = new JCheckBox("Activar Filtro de Tiempo");

    JComboBox<String> cbAccion = ThemeManager.StringComboBox();
    JTextField tfTabla = ThemeManager.Textfield();

    JLabel lDesde = ThemeManager.Label("Desde");
    JLabel lHasta = ThemeManager.Label("Hasta");

    JDateChooser dcDesde = new JDateChooser();
    JDateChooser dcHasta = new JDateChooser();

    JSpinner spHoraDesde = new JSpinner(new SpinnerDateModel());
    JSpinner spHoraHasta = new JSpinner(new SpinnerDateModel());

    JButton bBuscar = ThemeManager.Button("Buscar");

    ArrayList<JRegistroBitacora> JRegistros = new ArrayList<>();
    String[] headers = {"Nombre Completo", "Usuario/Cédula", "Acción", "Tabla Afectada", "Fecha Modificación"};
    //endregion

    //region Theme
    public void SetTheme() {
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        pFunctions.setPreferredSize(new Dimension(320, 0));
        pFunctions.setOpaque(false);
        pFunctions.setLayout(GBL);


        cbTime.setBackground(ThemeManager.COLOR_BACKGROUND);
        cbTime.setForeground(ThemeManager.COLOR_TEXT);

        lBusquedaFiltro.setFont(ThemeManager.TEXT_SUBTITLE);
        lBusquedaFiltro.setHorizontalAlignment(JLabel.CENTER);

        lDesde.setFont(ThemeManager.TEXT_SUBTITLE);
        lHasta.setFont(ThemeManager.TEXT_SUBTITLE);

        SetupDateChooser(dcDesde);
        SetupDateChooser(dcHasta);
        
        SetupTimeSpinner(spHoraDesde);
        SetupTimeSpinner(spHoraHasta);

        pTablaHeader.setBackground(ThemeManager.COLOR_PRIMARY); 
        pTablaHeader.setPreferredSize(new Dimension(0, 40));
        pTablaBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTabla.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        
        bBuscar.setPreferredSize(new Dimension(0, 40));
    }
    //endregion

    //region Configuration
    public MenuBitacora() {
        this.setLayout(new BorderLayout(20, 0));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SetTheme();
        
        AbstractDocument AD;
        AD = (AbstractDocument) tfNombreCompleto.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(60));

        AD = (AbstractDocument) tfUsuario.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(10));

        AD = (AbstractDocument) tfTabla.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(30));

        // ComboBox Acción
        cbAccion.addItem("Todas");
        cbAccion.addItem("Accesos");
        cbAccion.addItem("Bitacoras");
        cbAccion.addItem("Carnets");
        cbAccion.addItem("Cuotas");
        cbAccion.addItem("Pagos realizados");
        cbAccion.addItem("Representantes");
        cbAccion.addItem("Usuarios");
        cbAccion.addItem("Viviendas");
        cbAccion.setSelectedIndex(0);

        Calendar CAL = Calendar.getInstance();
        
        // Inicializar Hasta
        dcHasta.setDate(CAL.getTime());
        spHoraHasta.setValue(CAL.getTime());

        // Inicializar Desde (Hace 1 mes por defecto)
        CAL.add(Calendar.MONTH, -1); 
        dcDesde.setDate(CAL.getTime());
        spHoraDesde.setValue(CAL.getTime());

        GBC.weightx = 1;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.gridx = 0; 
        
        GBC.insets = new Insets(5, 0, 15, 0);
        GBC.gridy = 0; pFunctions.add(lBusquedaFiltro, GBC);

        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 1; pFunctions.add(FormRow(lNombreCompleto, tfNombreCompleto), GBC); 
        GBC.gridy = 2; pFunctions.add(FormRow(lUsuario, tfUsuario), GBC);
        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 3; 
        pFunctions.add(createRadioButtonPanel(), GBC);
        GBC.gridy = 4; pFunctions.add(FormRow(lTabla, cbAccion), GBC);
        
        GBC.gridy = 5; pFunctions.add(cbTime, GBC);

        GBC.insets = new Insets(10, 0, 10, 0);
        GBC.gridy = 6; pFunctions.add(DateTimeRow(lDesde, dcDesde, spHoraDesde), GBC);
        GBC.gridy = 7; pFunctions.add(DateTimeRow(lHasta, dcHasta, spHoraHasta), GBC);

        GBC.insets = new Insets(15, 0, 15, 0);
        GBC.gridy = 8; pFunctions.add(bBuscar, GBC);

        GBC.gridy = 9; GBC.weighty = 1.0;
        pFunctions.add(Box.createGlue(), GBC);

        pTabla.setLayout(new BorderLayout());
        pTablaHeader.setLayout(new GridLayout(1, 5));
        
        for (String h : headers) {
            JLabel lColumn = new JLabel(h, SwingConstants.LEFT);
            lColumn.setForeground(ThemeManager.COLOR_TEXT);
            lColumn.setFont(ThemeManager.TEXT_SUBTITLE);
            lColumn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            pTablaHeader.add(lColumn);
        }
        pTabla.add(pTablaHeader, BorderLayout.NORTH);
        
        pTablaBody.setLayout(GBL); 

        // Consulta inicial
        CargarBitacora();

        JScrollPane JSP = new JScrollPane(pTablaBody);
        JSP.setBorder(BorderFactory.createEmptyBorder());
        JSP.getViewport().setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        JSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pTabla.add(JSP, BorderLayout.CENTER);

        this.add(pFunctions, BorderLayout.WEST);
        this.add(pTabla, BorderLayout.CENTER);

        SetEvents();
    }
    //endregion
    private String getSelectedAccion() {
            if (rbTodos.isSelected()) {
                return "Todas";
            } else if (rbInsert.isSelected()) {
                return "INSERT";
            } else if (rbUpdate.isSelected()) {
                return "UPDATE";
            } else if (rbDelete.isSelected()) {
                return "DELETE";
            }
            return "Todas";
        }
    //region Tabla
    public void CargarBitacora() {
        // Validar que las fechas tengan sentido lógico
        if (dcDesde.getDate() != null && dcHasta.getDate() != null) {
            Calendar cDesde = Calendar.getInstance();
            cDesde.setTime(dcDesde.getDate());
            Calendar hDesde = Calendar.getInstance();
            hDesde.setTime((Date) spHoraDesde.getValue());
            cDesde.set(Calendar.HOUR_OF_DAY, hDesde.get(Calendar.HOUR_OF_DAY));
            cDesde.set(Calendar.MINUTE, hDesde.get(Calendar.MINUTE));
            cDesde.set(Calendar.SECOND, hDesde.get(Calendar.SECOND));

            Calendar cHasta = Calendar.getInstance();
            cHasta.setTime(dcHasta.getDate());
            Calendar hHasta = Calendar.getInstance();
            hHasta.setTime((Date) spHoraHasta.getValue());
            cHasta.set(Calendar.HOUR_OF_DAY, hHasta.get(Calendar.HOUR_OF_DAY));
            cHasta.set(Calendar.MINUTE, hHasta.get(Calendar.MINUTE));
            cHasta.set(Calendar.SECOND, hHasta.get(Calendar.SECOND));

            if (cDesde.getTimeInMillis() > cHasta.getTimeInMillis()) {
                ThemeManager.MostrarMensajeError(this, "La fecha \"Desde\" no puede ser posterior a la fecha \"Hasta\".");
                return; 
            }
        }

        JRegistros.clear();
        pTablaBody.removeAll();

        StringBuilder Query = new StringBuilder(
            "SELECT COALESCE(concat(u.nombre,' ',u.apellido), 'Sistema') AS nombre_completo, " +
            "b.usuario, b.accion, b.tabla_modificada, b.fecha_modificacion " +
            "FROM bitacoras b " +
            "LEFT JOIN usuarios u ON u.cedula = b.usuario " +
            "WHERE 1=1 "
        );

        ArrayList<Object> Parametros = new ArrayList<>();

        String sNombre = tfNombreCompleto.getText().trim();
        if (!sNombre.isEmpty()) {
            Query.append("AND concat(u.nombre,' ',u.apellido) ILIKE ? ");
            Parametros.add("%" + sNombre + "%");
        }

        String sUsuario = tfUsuario.getText().trim();
        if (!sUsuario.isEmpty()) {
            Query.append("AND b.usuario ILIKE ? ");
            Parametros.add("%" + sUsuario + "%");
        }

        
        String sAccion = getSelectedAccion();
        if (!"Todas".equals(sAccion)) {
            Query.append("AND b.accion = ? ");
            Parametros.add(sAccion);
        }

        
        String sTablas = (String) cbAccion.getSelectedItem();
        if (!"Todas".equals(sTablas)) {
            Query.append(" AND b.tabla_modificada = ? ");
            Parametros.add(sTablas.toLowerCase());
        }

        if (cbTime.isSelected()) {
            
        
        if (dcDesde.getDate() != null) {
            Calendar Fecha = Calendar.getInstance();
            Fecha.setTime(dcDesde.getDate());
            Calendar Hora = Calendar.getInstance();
            Hora.setTime((Date) spHoraDesde.getValue());
            Fecha.set(Calendar.HOUR_OF_DAY, Hora.get(Calendar.HOUR_OF_DAY));
            Fecha.set(Calendar.MINUTE, Hora.get(Calendar.MINUTE));
            Fecha.set(Calendar.SECOND, Hora.get(Calendar.SECOND));

            Query.append("AND b.fecha_modificacion >= ? ");
            Parametros.add(new Timestamp(Fecha.getTimeInMillis()));
        }
        
        if (dcHasta.getDate() != null) {
            Calendar Fecha = Calendar.getInstance();
            Fecha.setTime(dcHasta.getDate());
            Calendar Hora = Calendar.getInstance();
            Hora.setTime((Date) spHoraHasta.getValue());
            Fecha.set(Calendar.HOUR_OF_DAY, Hora.get(Calendar.HOUR_OF_DAY));
            Fecha.set(Calendar.MINUTE, Hora.get(Calendar.MINUTE));
            Fecha.set(Calendar.SECOND, Hora.get(Calendar.SECOND));

            Query.append("AND b.fecha_modificacion <= ? ");
            Parametros.add(new Timestamp(Fecha.getTimeInMillis()));
        }
        }
        Query.append("ORDER BY b.fecha_modificacion DESC;");

        try {
            Object[] paramsArray = Parametros.isEmpty() ? null : Parametros.toArray();
            ResultSet RS = ConexionPostgres.consultar(Query.toString(), paramsArray);
            
            while (RS != null && RS.next()) {
                String sNombreCompleto = RS.getString("nombre_completo");
                String lUsuario = RS.getString("usuario");
                String lAccion = RS.getString("accion");
                String lTabla = RS.getString("tabla_modificada");
                String sFecha = RS.getTimestamp("fecha_modificacion").toString();
                JRegistros.add(new JRegistroBitacora(sNombreCompleto, lUsuario, lAccion, lTabla, sFecha));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar bitácora: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }

        GridBagConstraints TablaGBC = new GridBagConstraints();
        TablaGBC.anchor = GridBagConstraints.NORTH; 
        TablaGBC.fill = GridBagConstraints.HORIZONTAL;
        TablaGBC.weightx = 1;

        for (int i = 0; i < JRegistros.size(); i++) {
            TablaGBC.gridy = i;
            TablaGBC.insets = new Insets(i == 0 ? 10 : 5, 10, 5, 10);
            pTablaBody.add(JRegistros.get(i).toPanel(), TablaGBC);
        }

        TablaGBC.gridy = 9999; 
        TablaGBC.weighty = 1.0;
        pTablaBody.add(Box.createGlue(), TablaGBC);

        pTablaBody.revalidate();
        pTablaBody.repaint();
    }
    //endregion

    //region Events
    private void SetEvents() {
        bBuscar.addActionListener(e -> CargarBitacora());
        /*
        tfNombreCompleto.addActionListener(e -> CargarBitacora());
        tfUsuario.addActionListener(e -> CargarBitacora());
        cbAccion.addActionListener(e -> CargarBitacora());
        tfTabla.addActionListener(e -> CargarBitacora());
        */

                cbTime.addActionListener(e ->{
        if (!cbTime.isSelected()) {
            dcDesde.setEnabled(false);
            dcHasta.setEnabled(false);
            spHoraDesde.setEnabled(false);
            spHoraHasta.setEnabled(false);
            
        }else{
            dcDesde.setEnabled(true);
            dcHasta.setEnabled(true);
            spHoraDesde.setEnabled(true);
            spHoraHasta.setEnabled(true);
        }

        });
    }
    //endregion

    //region Helper Functions
    private void SetupDateChooser(JDateChooser JDC) {
        JDC.setDateFormatString("dd/MM/yyyy");
        JDC.setOpaque(false);

        JTextField tfDate = (JTextField) JDC.getDateEditor().getUiComponent();
        tfDate.setEditable(false);
        tfDate.setBackground(ThemeManager.COLOR_INPUT);
        tfDate.setForeground(ThemeManager.COLOR_TEXT_DARK);
        tfDate.setFont(ThemeManager.TEXT_NORMAL);
        tfDate.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        tfDate.setPreferredSize(new Dimension(80, 25));

        JButton bCalendario = JDC.getCalendarButton();
        bCalendario.setBackground(ThemeManager.COLOR_PRIMARY);
        bCalendario.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        bCalendario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bCalendario.setPreferredSize(new Dimension(30, 25));
    }

    private void SetupTimeSpinner(JSpinner JTime) {
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(JTime, "HH:mm:ss");
        JTime.setEditor(timeEditor);
        JTime.setBorder(BorderFactory.createEmptyBorder());
        JTime.setBackground(ThemeManager.COLOR_INPUT);

        JTextField TF = timeEditor.getTextField();
        TF.setEditable(false); 
        
        TF.setBackground(ThemeManager.COLOR_INPUT);
        TF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        TF.setFont(ThemeManager.TEXT_NORMAL);
        TF.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        TF.setHorizontalAlignment(JTextField.CENTER);
    }
    

    private JPanel createRadioButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        
        // Configurar los radio buttons
        JRadioButton[] rbs = {rbTodos, rbInsert, rbUpdate, rbDelete};
        for (JRadioButton rb : rbs) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(ThemeManager.TEXT_NORMAL);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rb.addActionListener(e -> CargarBitacora()); // Auto-búsqueda al seleccionar
        }
        
        // Agregar al grupo
        bgAccion.add(rbTodos);
        bgAccion.add(rbInsert);
        bgAccion.add(rbUpdate);
        bgAccion.add(rbDelete);
        
        panel.add(rbTodos);
        panel.add(rbInsert);
        panel.add(rbUpdate);
        panel.add(rbDelete);
        
        return panel;
    }
    private JPanel FormRow(JLabel label, JComponent input) {
        JPanel JP = new JPanel(new BorderLayout(10, 0));
        JP.setOpaque(false);
        label.setPreferredSize(new Dimension(130, 25)); 
        JP.add(label, BorderLayout.WEST);
        JP.add(input, BorderLayout.CENTER);
        return JP;
    }

    private JPanel DateTimeRow(JLabel label, JDateChooser JDate, JSpinner JTime) {
        JPanel JP = new JPanel(new BorderLayout(10, 0));
        JP.setOpaque(false);
        label.setPreferredSize(new Dimension(75, 25)); 
        JP.add(label, BorderLayout.WEST);

        JDate.setPreferredSize(new Dimension(120, 25)); 
        JTime.setPreferredSize(new Dimension(80, 25)); 

        JPanel pInputs = new JPanel(new GridBagLayout());
        pInputs.setOpaque(false);
        GridBagConstraints DT_GBC = new GridBagConstraints();
        DT_GBC.fill = GridBagConstraints.HORIZONTAL;

        DT_GBC.weightx = 0.7;
        DT_GBC.gridx = 0;
        pInputs.add(JDate, DT_GBC);

        DT_GBC.insets = new Insets(0, 5, 0, 0);
        DT_GBC.weightx = 0.2;
        DT_GBC.gridx = 1;
        pInputs.add(JTime, DT_GBC);

        JP.add(pInputs, BorderLayout.CENTER);
        return JP;
    }
    //endregion
}