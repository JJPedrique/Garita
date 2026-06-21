package Frontend.ControlDeAcceso;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

//region JComponentes
class JRegistroAcceso {
    JLabel Carnet;
    JLabel Tipo;
    JLabel FechaUso;
    JLabel Estado;
    JLabel Nombre;
    JButton Opcion;

    public JRegistroAcceso(String Carnet, String Tipo, String FechaUso, String Estado, String Nombre) {
        this.Carnet = ThemeManager.Label(Carnet);
        this.Tipo = ThemeManager.Label(Tipo);
        this.FechaUso = ThemeManager.Label(FechaUso);
        this.Estado = ThemeManager.Label(Estado);
        this.Nombre = ThemeManager.Label(Nombre);
        this.Opcion = new JButton(ThemeManager.SetImgIcon("img\\config.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));

        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        this.Carnet.setHorizontalAlignment(SwingConstants.LEFT);
        this.Carnet.setBorder(margin);
        
        this.FechaUso.setHorizontalAlignment(SwingConstants.LEFT);
        this.FechaUso.setBorder(margin);
        
        this.Estado.setHorizontalAlignment(SwingConstants.CENTER);
        this.Estado.setOpaque(true);
        if(Estado.equalsIgnoreCase("Permitido")){
            this.Estado.setBackground(ThemeManager.COLOR_SECONDARY);
        } else {
            this.Estado.setBackground(ThemeManager.COLOR_ERROR);
        }
        this.Estado.setForeground(ThemeManager.COLOR_TEXT_DARK);
        
        this.Nombre.setHorizontalAlignment(SwingConstants.LEFT);
        this.Nombre.setBorder(margin);

        this.Opcion.setFocusPainted(false);
        this.Opcion.setContentAreaFilled(false);
        this.Opcion.setBorderPainted(false);
        this.Opcion.setForeground(Color.WHITE);
        this.Opcion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        
        ROW.add(Nombre);
        
        JPanel pBTN = new JPanel(new GridBagLayout());
        pBTN.setOpaque(false);
        pBTN.add(Opcion);
        ROW.add(pBTN);
        
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
    JLabel lNombreVisita = ThemeManager.Label("Nombre de Visita");

    JTextField tfCodigoCarnet = ThemeManager.Textfield();
    JTextField tfNombreVisita = ThemeManager.Textfield();

    JLabel lDesde = ThemeManager.Label("Desde");
    JLabel lHasta = ThemeManager.Label("Hasta");
    JDateChooser dcDesde = new JDateChooser();
    JDateChooser dcHasta = new JDateChooser();

    JRadioButton rbEstadoTodos = new JRadioButton("Todos", true);
    JRadioButton rbEstadoPermitido = new JRadioButton("Permitido");
    JRadioButton rbEstadoNegado = new JRadioButton("Negado");
    ButtonGroup bgEstado = new ButtonGroup();

    JRadioButton rbIdentTodos = new JRadioButton("Todos", true);
    JRadioButton rbIdentPropietario = new JRadioButton("Propietario");
    JRadioButton rbIdentVisitante = new JRadioButton("Visitante");
    ButtonGroup bgIdentificacion = new ButtonGroup();

    JButton bBuscar = ThemeManager.Button("Buscar");
    JSeparator hr = new JSeparator();
    JButton bSolicitarAccesoVisitante = ThemeManager.Button("Solicitar Acceso a Vigilante");

    ArrayList<JRegistroAcceso> JRegistros = new ArrayList<>();
    String[] headers = {"Carnet","Tipo de Acceso","Fecha de Uso", "Estado", "Nombre", "Opciones"};

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

        setupDateChooser(dcDesde);
        setupDateChooser(dcHasta);

        setupRadioButtons();

        pTablaHeader.setBackground(ThemeManager.COLOR_PRIMARY); 
        pTablaHeader.setPreferredSize(new Dimension(0, 40));
        pTablaBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTabla.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        
        hr.setForeground(ThemeManager.COLOR_INPUT);
        bBuscar.setPreferredSize(new Dimension(0, 40));
        bSolicitarAccesoVisitante.setPreferredSize(new Dimension(0, 40));
    }
    //endregion

    //region Configuracion
    public MenuRegistroDeAcceso() {
        this.setLayout(new BorderLayout(20, 0));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SetTheme();

        GBC.weightx = 1;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.gridx = 0; 
        
        GBC.insets = new Insets(5, 0, 15, 0);
        GBC.gridy = 0; pFunctions.add(lBusquedaFiltro, GBC);

        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 1; pFunctions.add(createFormRow(lCodigoCarnet, tfCodigoCarnet), GBC); 
        GBC.gridy = 2; pFunctions.add(createFormRow(lNombreVisita, tfNombreVisita), GBC);

        GBC.insets = new Insets(10, 0, 2, 0);
        GBC.gridy = 3; pFunctions.add(lFiltroEstado, GBC);

        JPanel pRadioEstado = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pRadioEstado.setOpaque(false);
        pRadioEstado.add(rbEstadoTodos); 
        pRadioEstado.add(rbEstadoPermitido); 
        pRadioEstado.add(rbEstadoNegado);
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

        GBC.insets = new Insets(5, 0, 5, 0);
        GBC.gridy = 7; pFunctions.add(createFormRow(lDesde, dcDesde), GBC);
        GBC.gridy = 8; pFunctions.add(createFormRow(lHasta, dcHasta), GBC);

        GBC.insets = new Insets(15, 0, 15, 0);
        GBC.gridy = 9; pFunctions.add(bBuscar, GBC);
        GBC.gridy = 10; pFunctions.add(hr, GBC);

        GBC.insets = new Insets(15, 0, 5, 0);
        GBC.gridy = 11; pFunctions.add(bSolicitarAccesoVisitante, GBC);

        GBC.gridy = 12; GBC.weighty = 1.0;
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
        ActualizarTabla();

        JScrollPane scrollPane = new JScrollPane(pTablaBody);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pTabla.add(scrollPane, BorderLayout.CENTER);

        this.add(pFunctions, BorderLayout.WEST);
        this.add(pTabla, BorderLayout.CENTER);
    }
    //endregion

    //region Tabla
    public void ActualizarTabla() {
        JRegistros.clear();
        pTablaBody.removeAll();

        String Query = "SELECT \n" + 
                        "COALESCE(C.codigo, 'INVITADO') AS codigo_carnet,\n" + 
                        "A.tipo AS tipo,\n" + 
                        "A.fecha_hora AS fecha,\n" + 
                        "A.estado AS estado,\n" + 
                        "COALESCE(A.nombre_visita, CONCAT(R.nombre, ' ', R.apellido)) AS nombre_completo\n" + 
                        "FROM accesos AS A \n" + 
                        "LEFT JOIN carnets AS C ON C.id = A.id_carnet\n" + 
                        "LEFT JOIN representantes AS R ON C.id_vivienda = R.id_vivienda\n" +
                        "ORDER BY A.fecha_hora DESC;";

        try {
            ConexionPostgres BDD = new ConexionPostgres();
            ResultSet RS = BDD.consultar(Query, null);
            
            while (RS != null && RS.next()) {
                String sCarnet = RS.getString("codigo_carnet");
                String sTipo = RS.getString("tipo");
                String sFecha = RS.getString("fecha");
                String sEstado = RS.getString("estado");
                String sNombre = RS.getString("nombre_completo");
                JRegistros.add(new JRegistroAcceso(sCarnet, sTipo, sFecha, sEstado, sNombre));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        GridBagConstraints tableGBC = new GridBagConstraints();
        tableGBC.anchor = GridBagConstraints.NORTH; 
        tableGBC.fill = GridBagConstraints.HORIZONTAL;
        tableGBC.weightx = 1;

        for (int i = 0; i < JRegistros.size(); i++) {
            tableGBC.gridy = i;
            tableGBC.insets = new Insets(i == 0 ? 10 : 5, 10, 5, 10);
            pTablaBody.add(JRegistros.get(i).toPanel(), tableGBC);
        }

        tableGBC.gridy = 9999; 
        tableGBC.weighty = 1.0;
        pTablaBody.add(Box.createGlue(), tableGBC);

        pTablaBody.revalidate();
        pTablaBody.repaint();
    }
    //endregion

    //region Helper Funcions
    private void setupDateChooser(JDateChooser chooser) {
        chooser.setDateFormatString("dd/MM/yy HH:mm:ss");
        chooser.setDate(new Date()); 
        
        JTextField editor = (JTextField) chooser.getDateEditor().getUiComponent();
        editor.setEditable(false);
        editor.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        editor.setForeground(ThemeManager.COLOR_TEXT);
        editor.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        chooser.setOpaque(false);
    }

    private void setupRadioButtons() {
        bgEstado.add(rbEstadoTodos); 
        bgEstado.add(rbEstadoPermitido); 
        bgEstado.add(rbEstadoNegado);

        bgIdentificacion.add(rbIdentTodos); 
        bgIdentificacion.add(rbIdentPropietario); 
        bgIdentificacion.add(rbIdentVisitante);

        JRadioButton[] rbs = {rbEstadoTodos, rbEstadoPermitido, rbEstadoNegado, rbIdentTodos, rbIdentPropietario, rbIdentVisitante};
        for (JRadioButton rb : rbs) {
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            rb.setFont(ThemeManager.TEXT_NORMAL);
        }
    }

    private JPanel createFormRow(JLabel label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        label.setPreferredSize(new Dimension(130, 25)); 
        panel.add(label, BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
    //endregion
}