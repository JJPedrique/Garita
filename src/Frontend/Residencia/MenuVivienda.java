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

public class MenuVivienda extends JPanel {

    private DefaultTableModel tableModel = new DefaultTableModel(
        new Object[]{"Num Vivienda", "Calle", "Estado", "Opciones"}, 0
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
    private JTextField txtNum;
    private JTextField txtCalle;

    private final JPanel pTable = new JPanel(new BorderLayout());
    private final JPanel pTableHeader = new JPanel(new GridLayout(1, 4));
    private final JPanel pTableBody = new JPanel(new GridBagLayout());
    private final ArrayList<ViviendaItem> viviendas = new ArrayList<>();
    private final String[] headers = {"Num Vivienda", "Calle", "Estado", "Opciones"};

    private static class ViviendaItem {
        private final int id;
        private final String numero;
        private final String calle;
        private final String estado;
        private final boolean activo;

        private ViviendaItem(int id, String numero, String calle, String estado, boolean activo) {
            this.id = id;
            this.numero = numero;
            this.calle = calle;
            this.estado = estado;
            this.activo = activo;
        }
    }

    public MenuVivienda() {
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
        btnAgregar.addActionListener(e -> abrirFormularioVivienda(false, null, null));

        JLabel tituloAgregar = new JLabel("<html><div style='text-align:center;'>AGREGAR NUEVA VIVIENDA</div></html>", SwingConstants.CENTER);
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
        panelFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lNum = new JLabel("Número de Vivienda");
        lNum.setFont(ThemeManager.TEXT_NORMAL);
        lNum.setForeground(ThemeManager.COLOR_TEXT);

        txtNum = ThemeManager.Textfield("Ej: A-11");

        JPanel pInputNum = new JPanel(new BorderLayout(10, 0));
        pInputNum.setOpaque(false);
        pInputNum.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputNum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputNum.add(lNum, BorderLayout.WEST);
        pInputNum.add(txtNum, BorderLayout.CENTER);

        JLabel lCalle = new JLabel("Calle");
        lCalle.setFont(ThemeManager.TEXT_NORMAL);
        lCalle.setForeground(ThemeManager.COLOR_TEXT);

        txtCalle = ThemeManager.Textfield("Ej: Calle Los Jabillos");

        JPanel pInputCalle = new JPanel(new BorderLayout(10, 0));
        pInputCalle.setOpaque(false);
        pInputCalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputCalle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputCalle.add(lCalle, BorderLayout.WEST);
        pInputCalle.add(txtCalle, BorderLayout.CENTER);

        panelFiltros.add(pInputNum);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(pInputCalle);

        JButton btnBuscar = ThemeManager.Button("Buscar");
        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBuscar.addActionListener(e -> cargarViviendas());

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

        cargarViviendas();
    }

    private void cargarViviendas() {
        StringBuilder sql = new StringBuilder(
            "SELECT v.id, v.numero_vivienda, v.calle, " +
            "CASE WHEN EXISTS (" +
            "    SELECT 1 FROM cuotas c " +
            "    WHERE c.activo = true " +
            "    AND c.fecha_limite < NOW() " +
            "    AND NOT EXISTS (" +
            "        SELECT 1 FROM pagos_realizados pr WHERE pr.id_cuota = c.id AND pr.id_vivienda = v.id" +
            "    )" +
            ") THEN 'Moroso' ELSE 'Solvente' END AS estado " +
            "FROM viviendas v"
        );

        boolean tieneNum = txtNum != null && !txtNum.getText().trim().isEmpty();
        boolean tieneCalle = txtCalle != null && !txtCalle.getText().trim().isEmpty();

        List<String> whereClauses = new ArrayList<>();
        List<Object> paramsList = new ArrayList<>();

        whereClauses.add("activo = true");

        if (tieneNum) {
            whereClauses.add("numero_vivienda ILIKE ?");
            paramsList.add("%" + txtNum.getText().trim() + "%");
        }

        if (tieneCalle) {
            whereClauses.add("calle ILIKE ?");
            paramsList.add("%" + txtCalle.getText().trim() + "%");
        }

        if (!whereClauses.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereClauses));
        }

        sql.append(" ORDER BY numero_vivienda");
        Object[] parametros = paramsList.isEmpty() ? null : paramsList.toArray();

        try {
            ResultSet rs = ConexionPostgres.consultar(sql.toString(), parametros);
            viviendas.clear();

            while (rs != null && rs.next()) {
                viviendas.add(new ViviendaItem(
                    rs.getInt("id"),
                    rs.getString("numero_vivienda"),
                    rs.getString("calle"),
                    rs.getString("estado"),
                    true
                ));
            }
            actualizarTablaViviendas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las viviendas: " + ex.getMessage());
        }
    }

    private void actualizarTablaViviendas() {
        pTableBody.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10);

        for (ViviendaItem vivienda : viviendas) {
            pTableBody.add(crearFilaVivienda(vivienda), gbc);
            gbc.gridy += 1;
        }

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        pTableBody.add(new JLabel(""), gbc);
        pTableBody.revalidate();
        pTableBody.repaint();
    }

    private JPanel crearFilaVivienda(ViviendaItem vivienda) {
        JPanel fila = new JPanel(new GridLayout(1, 4));
        fila.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        fila.setPreferredSize(new Dimension(0, 45));
        fila.setMinimumSize(new Dimension(0, 45));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        fila.add(crearCeldaTexto(vivienda.numero, false));
        fila.add(crearCeldaTexto(vivienda.calle, false));
        fila.add(crearEstado(vivienda.estado, vivienda.activo));
        fila.add(crearAccionesVivienda(vivienda));
        return fila;
    }

    private JLabel crearCeldaTexto(String texto, boolean centrado) {
        JLabel label = ThemeManager.Label(texto);
        label.setHorizontalAlignment(centrado ? SwingConstants.CENTER : SwingConstants.LEFT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        return label;
    }

    private JPanel crearEstado(String texto, boolean activo) {
        JLabel label = ThemeManager.Label(texto);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(ThemeManager.TEXT_SUBTITLE);

        boolean esPositivo;
        if ("Solvente".equalsIgnoreCase(texto)) {
            esPositivo = true;
        } else if ("Moroso".equalsIgnoreCase(texto)) {
            esPositivo = false;
        } else {
            esPositivo = activo;
        }
        label.setBackground(esPositivo ? ThemeManager.COLOR_ESTADO_LABEL_TRUE : ThemeManager.COLOR_ESTADO_LABEL_FALSE);
        label.setForeground(esPositivo ? ThemeManager.COLOR_ESTADO_TEXT_TRUE : ThemeManager.COLOR_ESTADO_TEXT_FALSE);

        label.setPreferredSize(new Dimension(90, 25));

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);
        contenedor.add(label);
        return contenedor;
    }

    private JPanel crearAccionesVivienda(ViviendaItem vivienda) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panel.setOpaque(false);

        JButton btnPagar = crearBotonIcono("img\\dolar.png");
        JButton btnVerDeudas = crearBotonIcono("img\\tabla-de-crecimiento.png");
        JButton btnEditar = crearBotonIcono("img\\edit.png");
        JButton btnEliminar = crearBotonIcono("img\\delete.png");

        btnPagar.addActionListener(e -> abrirVentanaPagoCuota(vivienda.id, vivienda.numero, vivienda.calle));
        btnVerDeudas.addActionListener(e -> mostrarCuotasPendientes(vivienda.id, vivienda.numero, vivienda.calle));
        btnEditar.addActionListener(e -> editarVivienda(vivienda.numero, vivienda.calle, vivienda.activo));
        btnEliminar.addActionListener(e -> cambiarEstadoVivienda(vivienda.numero, vivienda.activo));

        panel.add(btnPagar);
        panel.add(btnVerDeudas);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        return panel;
    }


    private void abrirVentanaPagoCuota(int idVivienda, String numeroVivienda, String calle) {
        CuotasService.CuotaPendiente cuota;
        try {
            cuota = CuotasService.obtenerCuotaActivaPendiente(idVivienda);
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo consultar la cuota activa: " + ex.getMessage());
            return;
        }

        if (cuota == null) {
            FrameMensaje.error(this, "No hay cuota activa pendiente para esta vivienda.");
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(owner, "Sistema Garita - Pagar Cuota", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(460, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FramePagarCuota(dialogo, idVivienda, numeroVivienda, calle, cuota, this::cargarViviendas));
        dialogo.setVisible(true);
    }

    private void mostrarCuotasPendientes(int idVivienda, String numeroVivienda, String calle) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(owner, "Sistema Garita - Cuotas Pendientes", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(760, 460);
        dialogo.setLocationRelativeTo(this);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FrameCuotasPendientes(dialogo, idVivienda, numeroVivienda, calle));
        dialogo.setVisible(true);
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

    private void editarVivienda(String numeroVivienda, String calleActual, boolean activoActual) {
        abrirFormularioVivienda(true, numeroVivienda, calleActual);
    }

    private void cambiarEstadoVivienda(String numeroVivienda, boolean activoActual) {
        boolean confirmar = ThemeManager.MostrarConfirmacion(
            this,
            "Sistema Garita - Eliminar Vivienda",
            "¿Desea eliminar la vivienda " + numeroVivienda + "? Se eliminará permanentemente.",
            ThemeManager.COLOR_ERROR,
            "Eliminar",
            "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";

            ConexionPostgres.comandoDML(
                "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE viviendas SET activo = false WHERE numero_vivienda = ?",
                new Object[]{numeroVivienda}
            );
            cargarViviendas();
            FrameMensaje.exitoEliminacion(this, "La vivienda ha sido eliminada con éxito.");
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo desactivar la vivienda: " + ex.getMessage());
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
            btnEliminar.setText("Eliminar");
            setBackground(isSelected ? ThemeManager.COLOR_BACKGROUND : ThemeManager.COLOR_BACKGROUND_LIGHT);
            return this;
        }
    }

    private class OpcionesEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton btnEditar = crearBoton("Editar");
        private final JButton btnEliminar = crearBoton("Eliminar");
        private String numeroVivienda;
        private String calle;
        private boolean activo;

        OpcionesEditor() {
            panel.setOpaque(true);
            panel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            panel.add(btnEditar);
            panel.add(btnEliminar);

            btnEditar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    editarVivienda(numeroVivienda, calle, activo);
                }
            });

            btnEliminar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    cambiarEstadoVivienda(numeroVivienda, activo);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            numeroVivienda = String.valueOf(table.getValueAt(row, 0));
            calle = String.valueOf(table.getValueAt(row, 1));
            activo = "Activo".equalsIgnoreCase(String.valueOf(table.getValueAt(row, 2)));
            btnEliminar.setText("Eliminar");
            panel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return numeroVivienda;
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

    private void abrirFormularioVivienda(boolean esEdicion, String numeroOriginal, String calleInicial) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(owner, esEdicion ? "Sistema Garita - Actualizar Vivienda" : "Sistema Garita - Agregar Vivienda", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(480, 320);
        dialogo.setLocationRelativeTo(this);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FrameFormularioVivienda(dialogo, esEdicion, numeroOriginal, calleInicial, this::cargarViviendas));
        dialogo.setVisible(true);
    }
}
