package Frontend.Cuotas;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import Backend.ConexionPostgres;
import Backend.ThemeManager;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// =======================================================================
// COMPONENTE TARJETA: Filas con GridBagLayout y Pesos Estrictos
// =======================================================================
class JTarjetaCuota {
    String id;
    String descripcionOriginal;
    String montoOriginal;
    String fechaLimiteOriginal;

    JPanel panelRow;
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
        this.Monto = ThemeManager.Label(monto + " $");
        this.FechaEmision = ThemeManager.Label(fEmision);
        this.FechaLimite = ThemeManager.Label(fLimite);
        this.Estado = ThemeManager.Label(activo ? "Activo" : "Inactivo");
        
        // Estilo del Estado
        this.Estado.setOpaque(true);
        this.Estado.setHorizontalAlignment(SwingConstants.CENTER);
        if (activo) {
            this.Estado.setBackground(new Color(46, 125, 50, 40));
            this.Estado.setForeground(new Color(129, 199, 132));
        } else {
            this.Estado.setBackground(new Color(198, 40, 40, 40));
            this.Estado.setForeground(new Color(240, 128, 128));
        }

        this.Editar = new JButton(ThemeManager.SetImgIcon("img\\edit.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
        this.Editar.setFocusPainted(false);
        this.Editar.setContentAreaFilled(false);
        this.Editar.setBorderPainted(false);
        this.Editar.setCursor(new Cursor(Cursor.HAND_CURSOR));

    this.Editar.addActionListener(e -> {
                JFrame frameAncestro = (JFrame) SwingUtilities.getWindowAncestor(panelRow);
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
        this.Borrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        this.Descripcion.setBorder(margin);
        this.Monto.setBorder(margin);
        this.FechaEmision.setBorder(margin);
        this.FechaLimite.setBorder(margin);

        this.Borrar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(
                null, 
                "¿Seguro que desea eliminar la cuota: \"" + desc + "\"?\nEsta acción no se puede deshacer.", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );
            if (opcion == JOptionPane.YES_OPTION) {
                try {
                    ConexionPostgres DB = new ConexionPostgres();
                    
                    
                    String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
                    if (miUsuario == null) miUsuario = "Sistema_Java";
                    
                    Object[] parametros = { 
                        id         
                    };
                    String queryDelete = "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "DELETE FROM cuotas WHERE id = ?::integer";
                    DB.comandoDML(queryDelete, parametros);
                    menuPadre.Search();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar cuota: " + ex.getMessage());
                }
            }
        });

        // Contenedor de la fila
        this.panelRow = new JPanel(new GridBagLayout());
        this.panelRow.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        this.panelRow.setPreferredSize(new Dimension(0, 52));
        this.panelRow.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        gbc.gridy = 0;

        // Distribución en columnas con pesos explícitos asignados a gbc.weightx en cada paso
        gbc.weightx = 0.30; gbc.gridx = 0; panelRow.add(Descripcion, gbc);
        gbc.weightx = 0.15; gbc.gridx = 1; panelRow.add(Monto, gbc);
        gbc.weightx = 0.20; gbc.gridx = 2; panelRow.add(FechaEmision, gbc);
        gbc.weightx = 0.20; gbc.gridx = 3; panelRow.add(FechaLimite, gbc);
        
        // Estado (Centrado y con margen interno)
        gbc.weightx = 0.10; gbc.gridx = 4; gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(10, 5, 10, 5); panelRow.add(Estado, gbc);
        
        //Botón Editar
        gbc.insets = new Insets(0, 0, 0, 0); 
        gbc.weightx = 0.04; gbc.gridx = 5; 
        gbc.fill = GridBagConstraints.NONE; 
        panelRow.add(Editar, gbc);


        // Botón Borrar
        gbc.insets = new Insets(0, 0, 0, 0); gbc.weightx = 0.05; gbc.gridx = 5; 
            gbc.weightx = 0.04; gbc.gridx = 6; 
        gbc.fill = GridBagConstraints.NONE; panelRow.add(Borrar, gbc);

        this.panelRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                                   
                                    Editar.doClick(); 
                                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                panelRow.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panelRow.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            }
        });
    }

    public JPanel toPanel() {
        return this.panelRow;
    }
}

// =======================================================================
// INTERFAZ PRINCIPAL CON CABECERA ALINEADA
// =======================================================================
public class MenuCuotas extends JPanel {

    ConexionPostgres DB = new ConexionPostgres();

    JButton bAgregarCuota = ThemeManager.Button("Añadir Cuota");
    JTextField inputDescripcion = ThemeManager.Textfield();
    JTextField inputMonto = ThemeManager.Textfield();
    
    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jsHoraDesde = createTimeSpinner();
    
    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jsHoraHasta = createTimeSpinner();

    private final JTextFieldDateEditor DesdeEditor = (JTextFieldDateEditor) jdcDesde.getDateEditor();
    

    private final JTextFieldDateEditor HastaEditor = (JTextFieldDateEditor) jdcHasta.getDateEditor();

    JRadioButton radioTodos = new JRadioButton("Todos");
    JRadioButton radioActivo = new JRadioButton("Activo");
    JRadioButton radioInactivo = new JRadioButton("Inactivo");
    ButtonGroup bgEstado = new ButtonGroup();
    
    JButton bBuscar = ThemeManager.Button("Buscar");

    JPanel pTableBody = new JPanel();
    JScrollPane scrollTable;
    ArrayList<JTarjetaCuota> JTarjetas = new ArrayList<>();

    public MenuCuotas() {
        //Evitar que las fechas sean editables
        DesdeEditor.setEditable(false);
        JSpinner.DefaultEditor editorHoraDesde = (JSpinner.DefaultEditor)  jsHoraDesde.getEditor();
        editorHoraDesde.getTextField().setEnabled(true);
        editorHoraDesde.getTextField().setEditable(false);

        HastaEditor.setEditable(false);
        JSpinner.DefaultEditor editorHoraHasta = (JSpinner.DefaultEditor)  jsHoraHasta.getEditor();
        editorHoraHasta.getTextField().setEnabled(true);
        editorHoraHasta.getTextField().setEditable(false);


        

        this.setLayout(new BorderLayout(15, 0));
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. PANEL LATERAL DE FILTROS
        JPanel pFiltros = new JPanel(new GridBagLayout());
        pFiltros.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pFiltros.setPreferredSize(new Dimension(250, 0));
        pFiltros.setBorder(new MatteBorder(0, 0, 0, 1, ThemeManager.COLOR_BACKGROUND_LIGHT));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        JLabel lTituloFiltros = ThemeManager.Label("GESTIÓN DE CUOTAS");
        lTituloFiltros.setFont(ThemeManager.TEXT_SUBTITLE);
        lTituloFiltros.setForeground(ThemeManager.COLOR_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(5, 5, 15, 15);
        pFiltros.add(lTituloFiltros, gbc);

        bAgregarCuota.setPreferredSize(new Dimension(0, 40));
        bAgregarCuota.setFont(ThemeManager.TEXT_SUBTITLE);
        gbc.gridy = 1; gbc.insets = new Insets(0, 5, 25, 15);
        pFiltros.add(bAgregarCuota, gbc);

        JLabel lFiltrosLabel = ThemeManager.Label("Filtros de Búsqueda");
        lFiltrosLabel.setFont(ThemeManager.TEXT_NORMAL);
        lFiltrosLabel.setForeground(Color.GRAY);
        gbc.gridy = 2; gbc.insets = new Insets(0, 5, 8, 15);
        pFiltros.add(lFiltrosLabel, gbc);

        JLabel lDesc = ThemeManager.Label("Descripción:");
        gbc.gridy = 3; gbc.insets = new Insets(4, 5, 2, 15);
        pFiltros.add(lDesc, gbc);
        inputDescripcion.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 4; gbc.insets = new Insets(0, 5, 10, 15);
        pFiltros.add(inputDescripcion, gbc);

        JLabel lMonto = ThemeManager.Label("Monto Máximo ($):");
        gbc.gridy = 5; gbc.insets = new Insets(4, 5, 2, 15);
        pFiltros.add(lMonto, gbc);
        inputMonto.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 6; gbc.insets = new Insets(0, 5, 10, 15);
        pFiltros.add(inputMonto, gbc);
        
        JLabel lDesde = ThemeManager.Label("Desde (Fecha Límite):");
        gbc.gridy = 7; gbc.insets = new Insets(4, 5, 2, 15);
        pFiltros.add(lDesde, gbc);
        jdcDesde.setPreferredSize(new Dimension(0, 32));
        SetupDateChooser(jdcDesde);
        gbc.gridy = 8; gbc.insets = new Insets(0, 5, 4, 15);
        pFiltros.add(jdcDesde, gbc);
        gbc.gridy = 9; gbc.insets = new Insets(0, 5, 12, 15);
        pFiltros.add(jsHoraDesde, gbc);

        JLabel lHasta = ThemeManager.Label("Hasta (Fecha Límite):");
        gbc.gridy = 10; gbc.insets = new Insets(4, 5, 2, 15);
        pFiltros.add(lHasta, gbc);
        jdcHasta.setPreferredSize(new Dimension(0, 32));
        SetupDateChooser(jdcHasta);
        gbc.gridy = 11; gbc.insets = new Insets(0, 5, 4, 15);
        pFiltros.add(jdcHasta, gbc);
        gbc.gridy = 12; gbc.insets = new Insets(0, 5, 12, 15);
        pFiltros.add(jsHoraHasta, gbc);

        JLabel lEstado = ThemeManager.Label("Estado:");
        gbc.gridy = 13; gbc.insets = new Insets(4, 5, 2, 15);
        pFiltros.add(lEstado, gbc);
        
        JPanel pRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pRadios.setOpaque(false);
        SetupRadioButtons();
        pRadios.add(radioTodos); pRadios.add(radioActivo); pRadios.add(radioInactivo);
        gbc.gridy = 14; gbc.insets = new Insets(0, 5, 20, 15);
        pFiltros.add(pRadios, gbc);

        bBuscar.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 15; gbc.insets = new Insets(5, 5, 5, 15);
        pFiltros.add(bBuscar, gbc);

        gbc.gridy = 16; gbc.weighty = 1;
        pFiltros.add(Box.createGlue(), gbc);

        this.add(pFiltros, BorderLayout.WEST);

        JPanel pCentral = new JPanel(new BorderLayout());
        pCentral.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        
        JPanel pTableHeader = new JPanel(new GridBagLayout());
        pTableHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pTableHeader.setPreferredSize(new Dimension(0, 40));

        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        headerGbc.weighty = 1;
        headerGbc.gridy = 0;

        String[] headers = {"Descripción", "Monto", "Fecha Emisión", "Fecha Límite", "Estado", "Acción"};
        double[] weights = {0.30, 0.15, 0.18, 0.18, 0.11, 0.08}; 

        for (int i = 0; i < headers.length; i++) {
            JLabel lbl = ThemeManager.Label(headers[i]);
            lbl.setFont(ThemeManager.TEXT_SUBTITLE);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            if (headers[i].equals("Estado") || headers[i].equals("Accion")) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            }
            
            headerGbc.weightx = weights[i];
            headerGbc.gridx = i;
            pTableHeader.add(lbl, headerGbc);
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
        Search();
    }

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(0, 32));
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

    private void SetupRadioButtons() {
        bgEstado.add(radioTodos); bgEstado.add(radioActivo); bgEstado.add(radioInactivo);
        radioTodos.setSelected(true);
        JRadioButton[] rbs = {radioTodos, radioActivo, radioInactivo};
        for (JRadioButton rb : rbs) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(ThemeManager.TEXT_NORMAL);
        }
    }

    private void RenderTable() {
        pTableBody.removeAll();
        GridBagConstraints GBC = new GridBagConstraints();
        GBC.anchor = GridBagConstraints.NORTH; 
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.gridwidth = 1; 
        GBC.weighty = 0;
        
        for (int i = 0; i < JTarjetas.size(); i++) {
            JTarjetaCuota actual = JTarjetas.get(i);
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

    public void Search() {
        JTarjetas.clear();
        String MAIN_QUERY = "SELECT id, descripcion, monto, fecha_emision, fecha_limite, activo FROM cuotas WHERE 1=1 ";
        ArrayList<Object> params = new ArrayList<>();

        String desc = inputDescripcion.getText().trim();
        if (!desc.isEmpty()) {
            MAIN_QUERY += "AND descripcion ILIKE ? ";
            params.add("%" + desc + "%");
        }

        String montoStr = inputMonto.getText().trim();
        if (!montoStr.isEmpty()) {
            try {
                double monto = Double.parseDouble(montoStr);
                MAIN_QUERY += "AND monto <= ? ";
                params.add(monto);
            } catch (NumberFormatException e) {}
        }

        if (jdcDesde.getDate() != null) {
            Calendar calFecha = Calendar.getInstance();
            calFecha.setTime(jdcDesde.getDate());
            Calendar calHora = Calendar.getInstance();
            calHora.setTime((Date) jsHoraDesde.getValue());
            calFecha.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            calFecha.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            calFecha.set(Calendar.SECOND, 0);
            
            MAIN_QUERY += "AND fecha_limite >= ? ";
            params.add(new java.sql.Timestamp(calFecha.getTimeInMillis()));
        }

        if (jdcHasta.getDate() != null) {
            Calendar calFecha = Calendar.getInstance();
            calFecha.setTime(jdcHasta.getDate());
            Calendar calHora = Calendar.getInstance();
            calHora.setTime((Date) jsHoraHasta.getValue());
            calFecha.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
            calFecha.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
            calFecha.set(Calendar.SECOND, 59);
            
            MAIN_QUERY += "AND fecha_limite <= ? ";
            params.add(new java.sql.Timestamp(calFecha.getTimeInMillis()));
        }

        if (radioActivo.isSelected()) {
            MAIN_QUERY += "AND activo = true ";
        } else if (radioInactivo.isSelected()) {
            MAIN_QUERY += "AND activo = false ";
        }

        MAIN_QUERY += "ORDER BY id DESC;";

        try {
            ResultSet rs = ConexionPostgres.consultar(MAIN_QUERY, params.isEmpty() ? null : params.toArray());
            while (rs != null && rs.next()) {
                JTarjetas.add(new JTarjetaCuota(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("monto"),
                    rs.getTimestamp("fecha_emision").toString(),
                    rs.getTimestamp("fecha_limite").toString(),
                    rs.getBoolean("activo"),
                    this
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar cuotas: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        RenderTable();
    }

    private void SetEvents() {
        bBuscar.addActionListener(e -> Search());
        inputDescripcion.addActionListener(e -> Search());
        inputMonto.addActionListener(e -> Search());

        bAgregarCuota.addActionListener(e -> {
            JFrame frameAncestro = (JFrame) SwingUtilities.getWindowAncestor(this);
            VentanaProgramarCuota vProgramar = new VentanaProgramarCuota(frameAncestro, this);
            vProgramar.setVisible(true);
        });
    }
}