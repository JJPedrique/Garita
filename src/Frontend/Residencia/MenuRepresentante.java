package Frontend.Residencia;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import Backend.ConexionPostgres;
import Backend.ThemeManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuRepresentante extends JPanel {

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[]{"Nombre Completo", "Cédula", "Teléfono", "Opciones"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 3 ? Object.class : String.class;
        }
    };

    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;

    private final JPanel pTable = new JPanel(new BorderLayout());
    private final JPanel pTableHeader = new JPanel(new GridLayout(1, 4));
    private final JPanel pTableBody = new JPanel(new GridBagLayout());
    private final ArrayList<RepresentanteItem> representantes = new ArrayList<>();
    private final String[] headers = {"Nombre Completo", "Cédula", "Teléfono", "Opciones"};

    private static class RepresentanteItem {
        private final String nombreCompleto;
        private final String cedula;
        private final String telefono;

        private RepresentanteItem(String nombreCompleto, String cedula, String telefono) {
            this.nombreCompleto = nombreCompleto;
            this.cedula = cedula;
            this.telefono = telefono;
        }
    }

    public MenuRepresentante() {
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(ThemeManager.COLOR_BACKGROUND);
        panelControles.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panelControles.setPreferredSize(new Dimension(315, 0));

        JButton btnAgregar = ThemeManager.Button("Agregar");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAgregar.addActionListener(e -> abrirFormularioRepresentante(false, null));

        JLabel tituloAgregar = new JLabel("<html><div style='text-align:center;'>AGREGAR NUEVO REPRESENTANTE</div></html>", SwingConstants.CENTER);
        tituloAgregar.setFont(ThemeManager.TEXT_SUBTITLE);
        tituloAgregar.setForeground(ThemeManager.COLOR_TEXT);
        tituloAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JSeparator separador = new JSeparator();
        separador.setForeground(ThemeManager.COLOR_INPUT);
        separador.setBackground(ThemeManager.COLOR_INPUT);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tituloBusqueda = new JLabel("BÚSQUEDA Y FILTROS", SwingConstants.CENTER);
        tituloBusqueda.setFont(ThemeManager.TEXT_SUBTITLE);
        tituloBusqueda.setForeground(ThemeManager.COLOR_TEXT);
        tituloBusqueda.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setBackground(ThemeManager.COLOR_BACKGROUND);
        panelFiltros.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel lCedula = new JLabel("Cédula");
        lCedula.setFont(ThemeManager.TEXT_NORMAL);
        lCedula.setForeground(ThemeManager.COLOR_TEXT);

        txtCedula = ThemeManager.Textfield("Ej: V-12345678");

        JPanel pInputCedula = new JPanel(new BorderLayout(10, 0));
        pInputCedula.setOpaque(false);
        pInputCedula.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputCedula.add(lCedula, BorderLayout.WEST);
        pInputCedula.add(txtCedula, BorderLayout.CENTER);

        JLabel lNombre = new JLabel("Nombre Completo");
        lNombre.setFont(ThemeManager.TEXT_NORMAL);
        lNombre.setForeground(ThemeManager.COLOR_TEXT);

        txtNombre = ThemeManager.Textfield("Ej: Carlos");

        JPanel pInputNombre = new JPanel(new BorderLayout(10, 0));
        pInputNombre.setOpaque(false);
        pInputNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputNombre.add(lNombre, BorderLayout.WEST);
        pInputNombre.add(txtNombre, BorderLayout.CENTER);

        JLabel lApellido = new JLabel("Apellido Completo");
        lApellido.setFont(ThemeManager.TEXT_NORMAL);
        lApellido.setForeground(ThemeManager.COLOR_TEXT);

        txtApellido = ThemeManager.Textfield("Ej: Mendoza");

        JPanel pInputApellido = new JPanel(new BorderLayout(10, 0));
        pInputApellido.setOpaque(false);
        pInputApellido.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputApellido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputApellido.add(lApellido, BorderLayout.WEST);
        pInputApellido.add(txtApellido, BorderLayout.CENTER);

        panelFiltros.add(pInputCedula);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(pInputNombre);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(pInputApellido);

        JButton btnBuscar = ThemeManager.Button("Buscar");
        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBuscar.addActionListener(e -> cargarRepresentantes());

        panelControles.add(tituloAgregar);
        panelControles.add(Box.createRigidArea(new Dimension(0, 12)));
        panelControles.add(btnAgregar);
        panelControles.add(Box.createRigidArea(new Dimension(0, 24)));
        panelControles.add(separador);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(tituloBusqueda);
        panelControles.add(Box.createRigidArea(new Dimension(0, 14)));
        panelControles.add(panelFiltros);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(btnBuscar);
        panelControles.add(Box.createVerticalGlue());

        this.add(panelControles, BorderLayout.WEST);

        pTable.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTableHeader.setBackground(ThemeManager.COLOR_PRIMARY);
        pTableHeader.setPreferredSize(new Dimension(0, 40));

        for (String header : headers) {
            int alignment = header.equals("Opciones") ? SwingConstants.CENTER : SwingConstants.LEFT;
            JLabel column = new JLabel(header, alignment);
            column.setForeground(ThemeManager.COLOR_TEXT);
            column.setFont(ThemeManager.TEXT_SUBTITLE);
            if (alignment == SwingConstants.LEFT) {
                column.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            }
            pTableHeader.add(column);
        }

        pTableBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTable.add(pTableHeader, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pTableBody);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pTable.add(scrollPane, BorderLayout.CENTER);

        this.add(pTable, BorderLayout.CENTER);

        cargarRepresentantes();
    }

    private void cargarRepresentantes() {
        StringBuilder sql = new StringBuilder(
            "SELECT concat(r.nombre, ' ', r.apellido) AS nombre_completo, r.cedula, r.telefono " +
            "FROM representantes r"
        );

        List<String> whereClauses = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        whereClauses.add("r.activo = true");

        if (txtCedula != null && !txtCedula.getText().trim().isEmpty()) {
            whereClauses.add("r.cedula ILIKE ?");
            parametros.add("%" + txtCedula.getText().trim() + "%");
        }

        if (txtNombre != null && !txtNombre.getText().trim().isEmpty()) {
            whereClauses.add("r.nombre ILIKE ?");
            parametros.add("%" + txtNombre.getText().trim() + "%");
        }

        if (txtApellido != null && !txtApellido.getText().trim().isEmpty()) {
            whereClauses.add("r.apellido ILIKE ?");
            parametros.add("%" + txtApellido.getText().trim() + "%");
        }

        sql.append(" WHERE ").append(String.join(" AND ", whereClauses));
        sql.append(" ORDER BY r.apellido, r.nombre");

        try {
            ResultSet rs = ConexionPostgres.consultar(sql.toString(), parametros.isEmpty() ? null : parametros.toArray());
            representantes.clear();

            while (rs != null && rs.next()) {
                representantes.add(new RepresentanteItem(
                    rs.getString("nombre_completo"),
                    rs.getString("cedula"),
                    rs.getString("telefono")
                ));
            }
            actualizarTablaRepresentantes();
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudieron cargar los representantes: " + ex.getMessage());
        }
    }

    private void actualizarTablaRepresentantes() {
        pTableBody.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10);

        for (RepresentanteItem representante : representantes) {
            pTableBody.add(crearFilaRepresentante(representante), gbc);
            gbc.gridy += 1;
        }

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        pTableBody.add(new JLabel(""), gbc);
        pTableBody.revalidate();
        pTableBody.repaint();
    }

    private JPanel crearFilaRepresentante(RepresentanteItem representante) {
        JPanel fila = new JPanel(new GridLayout(1, 4));
        fila.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        fila.setPreferredSize(new Dimension(0, 45));
        fila.setMinimumSize(new Dimension(0, 45));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        fila.add(crearCeldaTexto(representante.nombreCompleto));
        fila.add(crearCeldaTexto(representante.cedula));
        fila.add(crearCeldaTexto(representante.telefono));
        fila.add(crearAccionesRepresentante(representante));
        return fila;
    }

    private JLabel crearCeldaTexto(String texto) {
        JLabel label = ThemeManager.Label(texto);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        return label;
    }

    private JPanel crearAccionesRepresentante(RepresentanteItem representante) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panel.setOpaque(false);

        JButton btnEditar = crearBotonIcono("img\\edit.png");
        JButton btnEliminar = crearBotonIcono("img\\delete.png");

        btnEditar.addActionListener(e -> editarRepresentante(representante.cedula));
        btnEliminar.addActionListener(e -> cambiarEstadoRepresentante(representante.cedula));

        panel.add(btnEditar);
        panel.add(btnEliminar);
        return panel;
    }

    private JButton crearBotonIcono(String ruta) {
        JButton boton = new JButton(ThemeManager.SetImgIcon(ruta, ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setOpaque(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(28, 28));
        boton.setMaximumSize(new Dimension(28, 28));
        return boton;
    }

    private void editarRepresentante(String cedula) {
        FrameFormularioRepresentante.RepresentanteData data = obtenerRepresentantePorCedula(cedula);
        if (data == null) {
            FrameMensaje.error(this, "No se encontró el representante seleccionado.");
            return;
        }

        abrirFormularioRepresentante(true, data);
    }

    private void cambiarEstadoRepresentante(String cedula) {
        boolean confirmar = ThemeManager.MostrarConfirmacion(
            this,
            "Sistema Garita - Eliminar Representante",
            "¿Desea eliminar el representante " + cedula + "? Se desactivará permanentemente.",
            ThemeManager.COLOR_ERROR,
            "Eliminar",
            "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
        if (miUsuario == null) miUsuario = "Sistema_Java";

        try {
            ConexionPostgres.comandoDML(
                "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE representantes SET activo = false WHERE cedula = ?",
                new Object[]{cedula}
            );
            cargarRepresentantes();
            FrameMensaje.exitoEliminacion(this, "El representante ha sido eliminado con éxito.");
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo desactivar el representante: " + ex.getMessage());
        }
    }

    private class OpcionesRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnEditar = crearBoton("Editar");
        private final JButton btnEliminar = crearBoton("Eliminar");

        OpcionesRenderer() {
            setOpaque(true);
            setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
            add(btnEditar);
            add(btnEliminar);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? ThemeManager.COLOR_BACKGROUND : ThemeManager.COLOR_BACKGROUND_LIGHT);
            return this;
        }
    }

    private class OpcionesEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton btnEditar = crearBoton("Editar");
        private final JButton btnEliminar = crearBoton("Eliminar");
        private String cedula;

        OpcionesEditor() {
            panel.setOpaque(true);
            panel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            panel.add(btnEditar);
            panel.add(btnEliminar);

            btnEditar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    editarRepresentante(cedula);
                }
            });

            btnEliminar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    cambiarEstadoRepresentante(cedula);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            cedula = String.valueOf(table.getValueAt(row, 1));
            panel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return cedula;
        }
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(ThemeManager.TEXT_SMALL);
        boton.setForeground(ThemeManager.COLOR_TEXT);
        boton.setBackground(ThemeManager.COLOR_PRIMARY);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        boton.setPreferredSize(new Dimension(74, 24));
        return boton;
    }

    private void abrirFormularioRepresentante(boolean esEdicion, FrameFormularioRepresentante.RepresentanteData dataInicial) {
        List<FrameFormularioRepresentante.ViviendaComboItem> viviendas = FrameFormularioRepresentante.ViviendaComboItem.obtenerActivas();
        if (viviendas.isEmpty()) {
            FrameMensaje.error(this, "No hay viviendas activas disponibles para asociar el representante.");
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(owner, esEdicion ? "Sistema Garita - Actualizar Representante" : "Sistema Garita - Agregar Representante", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(560, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FrameFormularioRepresentante(dialogo, esEdicion, dataInicial, viviendas, this::cargarRepresentantes));
        dialogo.setVisible(true);
    }

    private FrameFormularioRepresentante.RepresentanteData obtenerRepresentantePorCedula(String cedula) {
        try {
            ResultSet rs = ConexionPostgres.consultar(
                "SELECT id_vivienda, nombre, apellido, cedula, telefono FROM representantes WHERE cedula = ? LIMIT 1",
                new Object[]{cedula}
            );

            if (rs != null && rs.next()) {
                return new FrameFormularioRepresentante.RepresentanteData(
                    rs.getInt("id_vivienda"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("cedula"),
                    rs.getString("telefono")
                );
            }
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo cargar el representante: " + ex.getMessage());
        }

        return null;
    }
}
