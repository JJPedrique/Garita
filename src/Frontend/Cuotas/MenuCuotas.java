package Frontend.Cuotas;

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
class JTarjetaCuota {
    String id;
    String descripcionOriginal;
    String montoOriginal;
    String fechaLimiteOriginal;

    JLabel Descripcion;
    JLabel Monto;
    JLabel FechaEmision;
    JLabel FechaLimite;
    JLabel Estado;
    JButton Borrar;
    JButton Editar;

    public JTarjetaCuota(String id, String desc, String monto, String fEmision, String fLimite, boolean activo, MenuCuotas menuPadre) {
        this.id = id;
        this.descripcionOriginal = desc;
        this.montoOriginal = monto;
        this.fechaLimiteOriginal = fLimite;

        this.Descripcion = ThemeManager.Label(desc);
        this.Monto = ThemeManager.Label("$" + monto);
        this.FechaEmision = ThemeManager.Label(fEmision);
        this.FechaLimite = ThemeManager.Label(fLimite);
        this.Estado = ThemeManager.Label(activo ? "Activo" : "Inactivo");
        
        // Estilo del Estado
        this.Estado.setOpaque(true);
        this.Estado.setHorizontalAlignment(SwingConstants.CENTER);
        this.Estado.setBackground(activo ? ThemeManager.COLOR_ESTADO_LABEL_TRUE : ThemeManager.COLOR_ESTADO_LABEL_FALSE);
        this.Estado.setForeground(activo ? ThemeManager.COLOR_ESTADO_TEXT_TRUE : ThemeManager.COLOR_ESTADO_TEXT_FALSE);
        this.Estado.setFont(ThemeManager.TEXT_SUBTITLE);
        this.Estado.setPreferredSize(new Dimension(90, 25));

        this.Editar = new JButton(ThemeManager.SetImgIcon("img\\edit.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
        this.Editar.setFocusPainted(false);
        this.Editar.setContentAreaFilled(false);
        this.Editar.setBorderPainted(false);
        this.Editar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.Editar.addActionListener(e -> {
            JFrame frameAncestro = (JFrame) SwingUtilities.getWindowAncestor(menuPadre);
            VentanaActualizarCuota vActualizar = new VentanaActualizarCuota(
                frameAncestro, 
                menuPadre, 
                descripcionOriginal, 
                montoOriginal, 
                fechaLimiteOriginal, 
                id
            );
            vActualizar.setVisible(true);
        });

        this.Borrar = new JButton(ThemeManager.SetImgIcon("img\\delete.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
        this.Borrar.setFocusPainted(false);
        this.Borrar.setContentAreaFilled(false);
        this.Borrar.setBorderPainted(false);
        this.Borrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.Borrar.addActionListener(e -> {
            boolean confirmar = ThemeManager.MostrarConfirmacion(
                        menuPadre,
                        "Sistema Garita - CONFIRMAR",
                        "¿Seguro que desea eliminar la cuota: \"" + desc + "\"?",
                        ThemeManager.COLOR_ERROR,
                        "Eliminar",
                        "Cancelar"
                    );
                    
                    if (confirmar) {
                        try {
                            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
                            if (miUsuario == null) miUsuario = "Sistema_Java";
                            
                            String queryDelete = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                            + "UPDATE cuotas set borrada = true, activo = false, descripcion = 'DEL_' || id WHERE id = ?::integer";
                            ConexionPostgres.comandoDML(queryDelete, new Object[]{id});
                            menuPadre.Search();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al eliminar cuota: " + ex.getMessage());
                        }
                    }
                });
        
    }
public JPanel toPanel() {
    // Usamos GridBagLayout para tener más control sobre el tamaño de las columnas
    JPanel ROW = new JPanel(new GridBagLayout());
    ROW.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
    ROW.setPreferredSize(new Dimension(0, 45));
    ROW.setMinimumSize(new Dimension(0, 45));
    ROW.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 2, 0, 2);
    gbc.weighty = 1.0;
    
    // Configuración de celdas de texto alineadas a la izquierda
    Border margin = BorderFactory.createEmptyBorder(0, 12, 0, 0);
    Descripcion.setHorizontalAlignment(SwingConstants.LEFT);
    Descripcion.setBorder(margin);
    
    Monto.setHorizontalAlignment(SwingConstants.LEFT);
    Monto.setBorder(margin);
    
    FechaEmision.setHorizontalAlignment(SwingConstants.LEFT);
    FechaEmision.setBorder(margin);
    
    FechaLimite.setHorizontalAlignment(SwingConstants.LEFT);
    FechaLimite.setBorder(margin);
    
    // Celda de Estado centrada
    Estado.setHorizontalAlignment(SwingConstants.CENTER);
    Estado.setPreferredSize(new Dimension(90, 25));
    JPanel pEstado = new JPanel(new GridBagLayout());
    pEstado.setOpaque(false);
    pEstado.add(Estado);
    
    // Celda de Acciones con FlowLayout centrado
    JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
    pBotones.setOpaque(false);
    pBotones.setPreferredSize(new Dimension(120, 30)); // Aumentamos el ancho
    pBotones.add(Editar);
    pBotones.add(Borrar);
    
    // Configurar pesos de las columnas (más peso para las columnas de texto)
    // Columna 0: Descripción (más peso)
    gbc.gridx = 0;
    gbc.weightx = 3.0;
    ROW.add(Descripcion, gbc);
    
    // Columna 1: Monto
    gbc.gridx = 1;
    gbc.weightx = 1.5;
    ROW.add(Monto, gbc);
    
    // Columna 2: Fecha Emisión
    gbc.gridx = 2;
    gbc.weightx = 1.5;
    ROW.add(FechaEmision, gbc);
    
    // Columna 3: Fecha Límite
    gbc.gridx = 3;
    gbc.weightx = 1.5;
    ROW.add(FechaLimite, gbc);
    
    // Columna 4: Estado
    gbc.gridx = 4;
    gbc.weightx = 1.0;
    ROW.add(pEstado, gbc);
    
    // Columna 5: Acciones (con suficiente espacio)
    gbc.gridx = 5;
    gbc.weightx = 1.5; // Peso similar al de las fechas
    ROW.add(pBotones, gbc);
    
    return ROW;
}
}


public class MenuCuotas extends JPanel {

    //region Componentes
    GridBagLayout GBL = new GridBagLayout();
    GridBagConstraints GBC = new GridBagConstraints();

    JPanel pFunctions = new JPanel();
    JPanel pTabla = new JPanel();
    JPanel pTablaHeader = new JPanel();
    JPanel pTablaBody = new JPanel();

    JLabel lGestionCuotas = ThemeManager.Label("GESTIÓN DE CUOTAS");
    JButton bAgregarCuota = ThemeManager.Button("Programar Cuota");

    JSeparator hr = new JSeparator();

    JLabel lBusquedaFiltro = ThemeManager.Label("BÚSQUEDA Y FILTROS");
    
    JLabel lDescripcion = ThemeManager.Label("Descripción");
    JLabel lMonto = ThemeManager.Label("Monto Máximo");

    JTextField tfDescripcion = ThemeManager.Textfield("Cuota ENE 2026");
    JTextField tfMonto = ThemeManager.Textfield("15.0");

    // Radio Buttons para Estado
    JRadioButton rbTodos = new JRadioButton("Todos", true);
    JRadioButton rbActivo = new JRadioButton("Activo");
    JRadioButton rbInactivo = new JRadioButton("Inactivo");
    ButtonGroup bgEstado = new ButtonGroup();

    JLabel lDesde = ThemeManager.Label("Desde");
    JLabel lHasta = ThemeManager.Label("Hasta");

    JCheckBox cbTime = new JCheckBox("Activar Filtro de Tiempo");

    JDateChooser dcDesde = new JDateChooser();
    JDateChooser dcHasta = new JDateChooser();

    JSpinner spHoraDesde = new JSpinner(new SpinnerDateModel());
    JSpinner spHoraHasta = new JSpinner(new SpinnerDateModel());

    JButton bBuscar = ThemeManager.Button("Buscar");

    ArrayList<JTarjetaCuota> JTarjetas = new ArrayList<>();
    String[] headers = {"Descripción", "Monto", "Fecha Emisión", "Fecha Límite", "Estado", "Acciones"};
    //endregion

    //region Theme
    public void SetTheme() {
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        pFunctions.setPreferredSize(new Dimension(320, 0));
        pFunctions.setOpaque(false);
        pFunctions.setLayout(GBL);

        lGestionCuotas.setFont(ThemeManager.TEXT_SUBTITLE);
        lGestionCuotas.setHorizontalAlignment(JLabel.CENTER);

        bAgregarCuota.setPreferredSize(new Dimension(0, 40));

        hr.setForeground(ThemeManager.COLOR_INPUT);

        lBusquedaFiltro.setFont(ThemeManager.TEXT_SUBTITLE);
        lBusquedaFiltro.setHorizontalAlignment(JLabel.CENTER);

        cbTime.setBackground(ThemeManager.COLOR_BACKGROUND);
        cbTime.setForeground(ThemeManager.COLOR_TEXT);

        lDescripcion.setFont(ThemeManager.TEXT_NORMAL);
        lMonto.setFont(ThemeManager.TEXT_NORMAL);

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
    public MenuCuotas() {
        this.setLayout(new BorderLayout(20, 0));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        dcDesde.setEnabled(false);
        dcHasta.setEnabled(false);
        spHoraDesde.setEnabled(false);
        spHoraHasta.setEnabled(false);
        
        SetTheme();
        SetupRadioButtons();
        
        AbstractDocument AD;
        AD = (AbstractDocument) tfDescripcion.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(40));

        AD = (AbstractDocument) tfMonto.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(10));

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
        
        // Título y Botón Agregar (arriba)
        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 0; pFunctions.add(lGestionCuotas, GBC);
        
        GBC.insets = new Insets(0, 0, 20, 0);
        GBC.gridy = 1; pFunctions.add(bAgregarCuota, GBC);
        
        GBC.insets = new Insets(0, 0, 20, 0);
        GBC.gridy = 2; pFunctions.add(hr, GBC);
        
        GBC.insets = new Insets(5, 0, 15, 0);
        GBC.gridy = 3; pFunctions.add(lBusquedaFiltro, GBC);

        // Filtros
        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 4; pFunctions.add(FormRow(lDescripcion, tfDescripcion), GBC); 
        GBC.gridy = 5; pFunctions.add(FormRow(lMonto, tfMonto), GBC);

        // Radio Buttons
        JPanel pRadios = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pRadios.setOpaque(false);
        pRadios.add(rbTodos);
        pRadios.add(rbActivo);
        pRadios.add(rbInactivo);
        GBC.insets = new Insets(5, 0, 10, 0);
        GBC.gridy = 6; pFunctions.add(pRadios, GBC);


        //Activar Tiempo
        GBC.gridy = 7; pFunctions.add(cbTime, GBC);

        // Fechas
        GBC.insets = new Insets(5, 0, 10, 0);
        GBC.gridy = 8; pFunctions.add(DateTimeRow(lDesde, dcDesde, spHoraDesde), GBC);
        GBC.gridy = 9; pFunctions.add(DateTimeRow(lHasta, dcHasta, spHoraHasta), GBC);

        // Botón Buscar
        GBC.insets = new Insets(15, 0, 15, 0);
        GBC.gridy = 10; pFunctions.add(bBuscar, GBC);

        GBC.gridy = 11; GBC.weighty = 1.0;
        pFunctions.add(Box.createGlue(), GBC);

        
        pTabla.setLayout(new BorderLayout());
        pTablaHeader.setLayout(new GridBagLayout());

        
        String[] headers = {"Descripción", "Monto", "Fecha Emisión", "Fecha Límite", "Estado", "Acciones"};
        double[] weights = {3.0, 1.5, 1.5, 1.5, 1.0, 1.5};

        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.fill = GridBagConstraints.BOTH;
        gbcHeader.insets = new Insets(0, 2, 0, 2);
        gbcHeader.weighty = 1.0;

        for (int i = 0; i < headers.length; i++) {
            String h = headers[i];
            int alignment = (h.equals("Estado") || h.equals("Acciones")) ? SwingConstants.CENTER : SwingConstants.LEFT;
            JLabel lColumn = new JLabel(h, alignment);
            lColumn.setForeground(ThemeManager.COLOR_TEXT);
            lColumn.setFont(ThemeManager.TEXT_SUBTITLE);
            
            if (alignment == SwingConstants.LEFT) {
                lColumn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            }
            
            gbcHeader.gridx = i;
            gbcHeader.weightx = weights[i];
            pTablaHeader.add(lColumn, gbcHeader);
        }
        pTabla.add(pTablaHeader, BorderLayout.NORTH);
        pTablaBody.setLayout(GBL); 

        // Consulta inicial
        Search();

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

    //region Tabla
    public void Search() {
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

        JTarjetas.clear();
        pTablaBody.removeAll();

        StringBuilder Query = new StringBuilder(
            "SELECT id, descripcion, monto, fecha_emision, fecha_limite, activo " +
            "FROM cuotas WHERE 1=1 AND borrada = false "
        );

        ArrayList<Object> Parametros = new ArrayList<>();

        String desc = tfDescripcion.getText().trim();
        if (!desc.isEmpty()) {
            Query.append("AND descripcion ILIKE ? ");
            Parametros.add("%" + desc + "%");
        }

        String montoStr = tfMonto.getText().trim();
        if (!montoStr.isEmpty()) {
            try {
                double monto = Double.parseDouble(montoStr);
                Query.append("AND monto <= ? ");
                Parametros.add(monto);
            } catch (NumberFormatException e) {
                // Ignorar si no es número válido
            }
        }



        // Radio Buttons Estado
        if (rbActivo.isSelected()) {
            Query.append("AND activo = true ");
        } else if (rbInactivo.isSelected()) {
            Query.append("AND activo = false ");
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

            Query.append("AND fecha_emision >= ? ");
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
            
            Query.append("AND fecha_limite <= ? ");
            Parametros.add(new Timestamp(Fecha.getTimeInMillis()));
        }
    }

        Query.append("ORDER BY id DESC;");

        try {
            Object[] paramsArray = Parametros.isEmpty() ? null : Parametros.toArray();
            ResultSet RS = ConexionPostgres.consultar(Query.toString(), paramsArray);

            
            
            while (RS != null && RS.next()) {
                JTarjetas.add(new JTarjetaCuota(
                    RS.getString("id"),
                    RS.getString("descripcion"),
                    RS.getString("monto"),
                    RS.getTimestamp("fecha_emision").toString().substring(0, 16),
                    RS.getTimestamp("fecha_limite").toString().substring(0, 16),
                    RS.getBoolean("activo"),
                    this
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar cuotas: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }

        GridBagConstraints TablaGBC = new GridBagConstraints();
        TablaGBC.anchor = GridBagConstraints.NORTH; 
        TablaGBC.fill = GridBagConstraints.HORIZONTAL;
        TablaGBC.weightx = 1;

        for (int i = 0; i < JTarjetas.size(); i++) {
            TablaGBC.gridy = i;
            TablaGBC.insets = new Insets(i == 0 ? 10 : 5, 10, 5, 10);
            pTablaBody.add(JTarjetas.get(i).toPanel(), TablaGBC);
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
        bBuscar.addActionListener(e -> Search());
        tfDescripcion.addActionListener(e -> Search());
        tfMonto.addActionListener(e -> Search());
        /*
        rbTodos.addActionListener(e -> Search());
        rbActivo.addActionListener(e -> Search());
        rbInactivo.addActionListener(e -> Search());
        */

        bAgregarCuota.addActionListener(e -> {
            JFrame frameAncestro = (JFrame) SwingUtilities.getWindowAncestor(this);
            VentanaProgramarCuota vProgramar = new VentanaProgramarCuota(frameAncestro, this);
            vProgramar.setVisible(true);
        });

        //Activar Tiempo 
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
    private void SetupRadioButtons() {
        bgEstado.add(rbTodos);
        bgEstado.add(rbActivo);
        bgEstado.add(rbInactivo);
        rbTodos.setSelected(true);

        JRadioButton[] rbs = {rbTodos, rbActivo, rbInactivo};
        for (JRadioButton rb : rbs) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(ThemeManager.TEXT_NORMAL);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
        }
    }

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
