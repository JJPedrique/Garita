package Frontend.ControlDeAcceso;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;


import Backend.ConexionPostgres;
import Backend.ThemeManager;
import Backend.ThemeManager.LimiteCaracteresFilter;

//region JComponentes
class JRegistroAcceso {
    JLabel Carnet;
    JLabel Tipo;
    JLabel FechaUso;
    JLabel Estado;
    JLabel Entidad;

    public JRegistroAcceso(String Carnet, String Tipo, String FechaUso, String Estado, String Entidad) {
        this.Carnet = ThemeManager.Label(Carnet);
        this.Tipo = ThemeManager.Label(Tipo);
        this.FechaUso = ThemeManager.Label(FechaUso);
        this.Estado = ThemeManager.Label(Estado);
        this.Entidad = ThemeManager.Label(Entidad);

        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        this.Carnet.setHorizontalAlignment(SwingConstants.LEFT);
        this.Carnet.setBorder(margin);
        
        this.FechaUso.setHorizontalAlignment(SwingConstants.LEFT);
        this.FechaUso.setBorder(margin);
        
        this.Estado.setHorizontalAlignment(SwingConstants.CENTER);
        this.Estado.setOpaque(true);

        this.Estado.setBackground((Estado.equalsIgnoreCase("Permitido")) ? ThemeManager.COLOR_ESTADO_LABEL_TRUE : ThemeManager.COLOR_ESTADO_LABEL_FALSE);
        this.Estado.setForeground((Estado.equalsIgnoreCase("Permitido")) ? ThemeManager.COLOR_ESTADO_TEXT_TRUE : ThemeManager.COLOR_ESTADO_TEXT_FALSE);
        this.Estado.setFont(ThemeManager.TEXT_SUBTITLE);
        
        this.Entidad.setHorizontalAlignment(SwingConstants.LEFT);
        this.Entidad.setBorder(margin);
    }

    public JPanel toPanel() {
        JPanel ROW = new JPanel(new GridLayout(1, 5));
        ROW.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        ROW.setPreferredSize(new Dimension(0, 45));
        ROW.setMinimumSize(new Dimension(0, 45));
        ROW.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        ROW.add(Carnet);
        ROW.add(Tipo);
        ROW.add(FechaUso);
        
        JPanel pEstado = new JPanel(new GridBagLayout());
        pEstado.setOpaque(false);
        Estado.setPreferredSize(new Dimension(90, 25));
        pEstado.add(Estado);
        ROW.add(pEstado);
        ROW.add(Entidad);
        return ROW;
    }
}
//endregion

public class MenuRegistroDeAcceso extends JPanel {

    //region Componentes
    GridBagLayout GBL = new GridBagLayout();
    GridBagConstraints GBC = new GridBagConstraints();

    JPanel pFunctions = new JPanel();
    JPanel pTabla = new JPanel();
    JPanel pTablaHeader = new JPanel();
    JPanel pTablaBody = new JPanel();

    JLabel lBusquedaFiltro = ThemeManager.Label("BÚSQUEDA Y FILTROS");
    JLabel lFiltroEstado = ThemeManager.Label("Filtro de estado");
    JLabel lFiltroIdentificacion = ThemeManager.Label("Filtro por identificación");

    JLabel lCodigoCarnet = ThemeManager.Label("Código de Carnet");
    JLabel lNombreEntidad = ThemeManager.Label("Nombre de Entidad");

    JTextField tfCodigoCarnet = ThemeManager.Textfield("0000000000");
    JTextField tfEntidad = ThemeManager.Textfield("XXB-YY / Juan");

    JCheckBox cbTiempoReal = new JCheckBox("Tiempo Real (Últimas 24h)", true);
    Timer tTiempoReal;

    JLabel lDesde = ThemeManager.Label("Desde");
    JLabel lHasta = ThemeManager.Label("Hasta");

    JDateChooser dcDesde = new JDateChooser();
    JDateChooser dcHasta = new JDateChooser();

    JSpinner spHoraDesde = new JSpinner(new SpinnerDateModel());
    JSpinner spHoraHasta = new JSpinner(new SpinnerDateModel());

    JRadioButton rbEstadoTodos = new JRadioButton("Todos", true);
    JRadioButton rbEstadoPermitido = new JRadioButton("Permitido");
    JRadioButton rbEstadoDenegado = new JRadioButton("Denegado");
    ButtonGroup bgEstado = new ButtonGroup();

    JRadioButton rbIdentTodos = new JRadioButton("Todos", true);
    JRadioButton rbIdentPropietario = new JRadioButton("Propietario");
    JRadioButton rbIdentVisitante = new JRadioButton("Visitante");
    ButtonGroup bgIdentificacion = new ButtonGroup();

    JButton bBuscar = ThemeManager.Button("Buscar");
    JSeparator hr = new JSeparator();
    JButton bSolicitarAccesoVisitante = ThemeManager.Button("Solicitar Acceso al Visitante");
    JButton bSolicitarAccesoResidente = ThemeManager.Button("Solicitar Acceso al Residente");

    ArrayList<JRegistroAcceso> JRegistros = new ArrayList<>();
    String[] headers = {"Carnet","Tipo de Acceso","Fecha de Uso", "Estado", "Entidad"};

    //region Theme
    public void SetTheme() {
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        pFunctions.setPreferredSize(new Dimension(320, 0));
        pFunctions.setOpaque(false);
        pFunctions.setLayout(GBL);

        lBusquedaFiltro.setFont(ThemeManager.TEXT_SUBTITLE);
        lBusquedaFiltro.setHorizontalAlignment(JLabel.CENTER);

        lFiltroEstado.setFont(ThemeManager.TEXT_SUBTITLE);
        lFiltroEstado.setHorizontalAlignment(JLabel.CENTER);

        lFiltroIdentificacion.setFont(ThemeManager.TEXT_SUBTITLE);
        lFiltroIdentificacion.setHorizontalAlignment(JLabel.CENTER);

        lDesde.setFont(ThemeManager.TEXT_SUBTITLE);
        lHasta.setFont(ThemeManager.TEXT_SUBTITLE);

        SetupDateChooser(dcDesde);
        SetupDateChooser(dcHasta);
        
        SetupTimeSpinner(spHoraDesde);
        SetupTimeSpinner(spHoraHasta);
        
        cbTiempoReal.setOpaque(false);
        cbTiempoReal.setForeground(Color.WHITE);
        cbTiempoReal.setFont(ThemeManager.TEXT_NORMAL);

        pTablaHeader.setBackground(ThemeManager.COLOR_PRIMARY); 
        pTablaHeader.setPreferredSize(new Dimension(0, 40));
        pTablaBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTabla.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        
        hr.setForeground(ThemeManager.COLOR_INPUT);
        bBuscar.setPreferredSize(new Dimension(0, 40));
        bSolicitarAccesoVisitante.setPreferredSize(new Dimension(0, 40));
        bSolicitarAccesoResidente.setPreferredSize(new Dimension(0, 40));
    }
    //endregion

    //region Configuration
    public MenuRegistroDeAcceso() {
        this.setLayout(new BorderLayout(20, 0));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SetTheme();
        SetupRadioBtns();
        SetEnableButtons();
        
        AbstractDocument AD;
        AD = (AbstractDocument) tfCodigoCarnet.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(10));

        AD = (AbstractDocument) tfEntidad.getDocument();
        AD.setDocumentFilter(new LimiteCaracteresFilter(40));

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
        GBC.gridy = 1; pFunctions.add(FormRow(lCodigoCarnet, tfCodigoCarnet), GBC); 
        GBC.gridy = 2; pFunctions.add(FormRow(lNombreEntidad, tfEntidad), GBC);

        GBC.insets = new Insets(10, 0, 2, 0);
        GBC.gridy = 3; pFunctions.add(lFiltroEstado, GBC);

        JPanel pRadioEstado = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pRadioEstado.setOpaque(false);
        pRadioEstado.add(rbEstadoTodos); 
        pRadioEstado.add(rbEstadoPermitido); 
        pRadioEstado.add(rbEstadoDenegado);
        GBC.insets = new Insets(0, 0, 10, 0);
        GBC.gridy = 4; pFunctions.add(pRadioEstado, GBC);

        GBC.insets = new Insets(5, 0, 2, 0);
        GBC.gridy = 5; pFunctions.add(lFiltroIdentificacion, GBC);

        JPanel pRadioIdent = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pRadioIdent.setOpaque(false);
        pRadioIdent.add(rbIdentTodos); 
        pRadioIdent.add(rbIdentPropietario); 
        pRadioIdent.add(rbIdentVisitante);
        GBC.insets = new Insets(0, 0, 10, 0);
        GBC.gridy = 6; pFunctions.add(pRadioIdent, GBC);

        GBC.insets = new Insets(5, 0, 10, 0);
        GBC.gridy = 7; pFunctions.add(cbTiempoReal, GBC);

        GBC.gridy = 8; pFunctions.add(DateTimeRow(lDesde, dcDesde, spHoraDesde), GBC);
        GBC.gridy = 9; pFunctions.add(DateTimeRow(lHasta, dcHasta, spHoraHasta), GBC);

        GBC.insets = new Insets(15, 0, 15, 0);
        GBC.gridy = 10; pFunctions.add(bBuscar, GBC);
        GBC.gridy = 11; pFunctions.add(hr, GBC);

        GBC.insets = new Insets(15, 0, 5, 0);
        GBC.gridy = 12; pFunctions.add(bSolicitarAccesoVisitante, GBC);

        GBC.insets = new Insets(15, 0, 5, 0);
        GBC.gridy = 13; pFunctions.add(bSolicitarAccesoResidente, GBC);

        GBC.gridy = 14; GBC.weighty = 1.0;
        pFunctions.add(Box.createGlue(), GBC);

        pTabla.setLayout(new BorderLayout());
        pTablaHeader.setLayout(new GridLayout(1, 5));
        
        for (String h : headers) {
            int alignment = (h.equals("Opciones") || h.equals("Estado")) ? SwingConstants.CENTER : SwingConstants.LEFT;
            JLabel lColumn = new JLabel(h, alignment);
            lColumn.setForeground(ThemeManager.COLOR_TEXT);
            lColumn.setFont(ThemeManager.TEXT_SUBTITLE);
            if (alignment == SwingConstants.LEFT) {
                lColumn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            }
            pTablaHeader.add(lColumn);
        }
        pTabla.add(pTablaHeader, BorderLayout.NORTH);
        
        pTablaBody.setLayout(GBL); 

        SetupTiempoRealTimer();

        // Consulta inicial
        ActualizarTabla(false);

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
    public void ActualizarTabla(boolean esAutomatica) {
        // Si no está en tiempo real, validamos que las fechas tengan sentido lógico
        if (!cbTiempoReal.isSelected() && dcDesde.getDate() != null && dcHasta.getDate() != null) {
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
                ThemeManager.MostrarMensajeError(this,"La fecha \"Desde\" no puede ser posterior a la fecha \"Hasta\".");
                return; 
            }
        }

        JRegistros.clear();
        pTablaBody.removeAll();

        StringBuilder Query = new StringBuilder(
            "SELECT \n" +
            "COALESCE(C.codigo, 'INVITADO') AS codigo_carnet,\n" +
            "A.tipo AS tipo,\n" +
            "A.fecha_hora AS fecha,\n" +
            "A.estado AS estado,\n" +
            "CASE\n" +
            "WHEN nombre_visita IS NULL THEN V.numero_vivienda\n" +
            "ELSE nombre_visita\n" +
            "END AS entidad\n" +
            "FROM accesos AS A \n" +
            "LEFT JOIN carnets AS C ON C.id = A.id_carnet\n" +
            "LEFT JOIN viviendas AS V ON V.id = C.id_vivienda " +
            "WHERE 1=1 "
        );

        ArrayList<Object> Parametros = new ArrayList<>();

        String sCodigo = tfCodigoCarnet.getText().trim().toUpperCase();
        if (!sCodigo.isEmpty()) {
            Query.append("AND C.codigo LIKE ? ");
            Parametros.add("%" + sCodigo + "%");
        }

        String sNombre = tfEntidad.getText().trim();
        if (!sNombre.isEmpty()) {
            Query.append("AND (A.nombre_visita ILIKE ? OR V.numero_vivienda ILIKE ?) ");
            String match = "%" + sNombre + "%";
            Parametros.add(match); Parametros.add(match);
        }

        if (rbEstadoPermitido.isSelected()) {
            Query.append("AND A.estado = 'Permitido' ");
        } else if (rbEstadoDenegado.isSelected()) {
            Query.append("AND A.estado = 'Denegado' ");
        }

        if (rbIdentPropietario.isSelected()) {
            Query.append("AND A.id_carnet IS NOT NULL ");
        } else if (rbIdentVisitante.isSelected()) {
            Query.append("AND A.id_carnet IS NULL ");
        }

        if (cbTiempoReal.isSelected()) {
            Calendar Ultimas24H = Calendar.getInstance();
            Ultimas24H.add(Calendar.DAY_OF_YEAR, -1);
            Query.append("AND A.fecha_hora >= ? ");
            Parametros.add(new Timestamp(Ultimas24H.getTimeInMillis()));
            
            Query.append("AND A.fecha_hora <= ? ");
            Parametros.add(new Timestamp(System.currentTimeMillis()));
        } else {
            if (dcDesde.getDate() != null) {
                Calendar Fecha = Calendar.getInstance();
                Fecha.setTime(dcDesde.getDate());
                Calendar Hora = Calendar.getInstance();
                Hora.setTime((Date) spHoraDesde.getValue());
                Fecha.set(Calendar.HOUR_OF_DAY, Hora.get(Calendar.HOUR_OF_DAY));
                Fecha.set(Calendar.MINUTE, Hora.get(Calendar.MINUTE));
                Fecha.set(Calendar.SECOND, Hora.get(Calendar.SECOND));

                Query.append("AND A.fecha_hora >= ? ");
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

                Query.append("AND A.fecha_hora <= ? ");
                Parametros.add(new Timestamp(Fecha.getTimeInMillis()));
            }
        }

        Query.append("ORDER BY A.fecha_hora DESC;");

        try {
            Object[] paramsArray = Parametros.isEmpty() ? null : Parametros.toArray();
            ResultSet RS = ConexionPostgres.consultar(Query.toString(), paramsArray);
            
            while (RS != null && RS.next()) {
                String sCarnet = RS.getString("codigo_carnet");
                String sTipo = RS.getString("tipo");
                String sFecha = RS.getString("fecha");
                String sEstado = RS.getString("estado");
                String sEntidad = RS.getString("entidad");
                JRegistros.add(new JRegistroAcceso(sCarnet, sTipo, sFecha, sEstado, sEntidad));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
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
        bBuscar.addActionListener(e -> ActualizarTabla(false));

        cbTiempoReal.addActionListener(e -> {
            SetEnableButtons(); 
            if (cbTiempoReal.isSelected()) {
                if (!tTiempoReal.isRunning()) {
                    tTiempoReal.start(); 
                }
            } else {
                tTiempoReal.stop(); 
            }
            ActualizarTabla(false);
        });

        bSolicitarAccesoVisitante.addActionListener(e -> {
            JDialog JDAccesoVisitante = new JDialog((Window) SwingUtilities.getWindowAncestor(this), "Sistema Garita - Solicitar Acceso Visitante", Dialog.ModalityType.APPLICATION_MODAL);
            JDAccesoVisitante.setSize(new Dimension(650, 500));
            JDAccesoVisitante.setLocationRelativeTo(this);
            JDAccesoVisitante.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            JDAccesoVisitante.add(new FrameSolicitarAccesoVisitante(JDAccesoVisitante));
            JDAccesoVisitante.setVisible(true);
            ActualizarTabla(false); 
        });
        bSolicitarAccesoResidente.addActionListener(e -> {
            JDialog JDAccesoVisitante = new JDialog((Window) SwingUtilities.getWindowAncestor(this), "Sistema Garita - Solicitar Acceso Residente", Dialog.ModalityType.APPLICATION_MODAL);
            JDAccesoVisitante.setSize(new Dimension(650, 400));
            JDAccesoVisitante.setLocationRelativeTo(this);
            JDAccesoVisitante.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            JDAccesoVisitante.add(new FrameSolicitarAccesoResidente(JDAccesoVisitante));
            JDAccesoVisitante.setVisible(true);
            ActualizarTabla(false); 
        });
    }

    private void SetupTiempoRealTimer() {
        tTiempoReal = new Timer(1000, e -> {
            if (cbTiempoReal.isSelected()) {

                Calendar ahora = Calendar.getInstance();
                dcHasta.setDate(ahora.getTime());
                spHoraHasta.setValue(ahora.getTime());
                
                ActualizarTabla(true);
            }
        });
        
        if (cbTiempoReal.isSelected()) {
            tTiempoReal.start();
        }
    }
    //endregion

    //region Helper Functions
    private void SetEnableButtons(){
        boolean enTiempoReal = cbTiempoReal.isSelected();

        dcDesde.setEnabled(!enTiempoReal);
        spHoraDesde.setEnabled(!enTiempoReal);
        dcHasta.setEnabled(!enTiempoReal);
        spHoraHasta.setEnabled(!enTiempoReal);
        dcDesde.getCalendarButton().setEnabled(!enTiempoReal);
        dcHasta.getCalendarButton().setEnabled(!enTiempoReal);
        bBuscar.setEnabled(!enTiempoReal);
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

    private void SetupRadioBtns() {
        bgEstado.add(rbEstadoTodos); 
        bgEstado.add(rbEstadoPermitido); 
        bgEstado.add(rbEstadoDenegado);

        bgIdentificacion.add(rbIdentTodos); 
        bgIdentificacion.add(rbIdentPropietario); 
        bgIdentificacion.add(rbIdentVisitante);

        JRadioButton[] rbs = {rbEstadoTodos, rbEstadoPermitido, rbEstadoDenegado, rbIdentTodos, rbIdentPropietario, rbIdentVisitante};
        for (JRadioButton rb : rbs) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(ThemeManager.TEXT_NORMAL);
        }
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