package Frontend.Residencia;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private static final int SIDEBAR_WIDTH = 190;
    private static final int BUTTON_HEIGHT = 30;
    private static final int INPUT_HEIGHT = 24;

    private final ConexionPostgres db = new ConexionPostgres();

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

    private final JTable table = new JTable(tableModel);
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;

    public MenuRepresentante() {
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(ThemeManager.COLOR_BACKGROUND);
        panelControles.setBorder(BorderFactory.createEmptyBorder(20, 8, 18, 8));
        panelControles.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JButton btnAgregar = ThemeManager.Button("Agregar");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregar.setPreferredSize(new Dimension(136, BUTTON_HEIGHT));
        btnAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        btnAgregar.addActionListener(e -> abrirFormularioRepresentante(false, null));

        JLabel tituloAgregar = new JLabel("<html><div style='text-align:center;'>AGREGAR NUEVO REPRESENTANTE</div></html>", SwingConstants.CENTER);
        tituloAgregar.setFont(ThemeManager.TEXT_SMALL);
        tituloAgregar.setForeground(ThemeManager.COLOR_TEXT);
        tituloAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JSeparator separador = new JSeparator();
        separador.setForeground(ThemeManager.COLOR_INFO);
        separador.setBackground(ThemeManager.COLOR_INFO);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tituloBusqueda = new JLabel("BÚSQUEDA Y FILTROS", SwingConstants.CENTER);
        tituloBusqueda.setFont(ThemeManager.TEXT_SMALL);
        tituloBusqueda.setForeground(ThemeManager.COLOR_TEXT);
        tituloBusqueda.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setBackground(ThemeManager.COLOR_BACKGROUND);
        panelFiltros.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel lCedula = new JLabel("Cédula");
        lCedula.setFont(ThemeManager.TEXT_SMALL);
        lCedula.setForeground(ThemeManager.COLOR_TEXT);
        lCedula.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCedula = crearCampoTexto();

        JLabel lNombre = new JLabel("Nombre Completo");
        lNombre.setFont(ThemeManager.TEXT_SMALL);
        lNombre.setForeground(ThemeManager.COLOR_TEXT);
        lNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNombre = crearCampoTexto();

        JLabel lApellido = new JLabel("Apellido Completo");
        lApellido.setFont(ThemeManager.TEXT_SMALL);
        lApellido.setForeground(ThemeManager.COLOR_TEXT);
        lApellido.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtApellido = crearCampoTexto();

        panelFiltros.add(lCedula);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 6)));
        panelFiltros.add(txtCedula);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(lNombre);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 6)));
        panelFiltros.add(txtNombre);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(lApellido);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 6)));
        panelFiltros.add(txtApellido);

        JButton btnBuscar = ThemeManager.Button("Buscar");
        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBuscar.setPreferredSize(new Dimension(136, BUTTON_HEIGHT));
        btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
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

        table.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        table.setForeground(Color.WHITE);
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setCellRenderer(new OpcionesRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new OpcionesEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        cargarRepresentantes();
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(140, INPUT_HEIGHT));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));
        campo.setBackground(ThemeManager.COLOR_INPUT);
        campo.setForeground(ThemeManager.COLOR_TEXT_DARK);
        campo.setBorder(BorderFactory.createLineBorder(ThemeManager.COLOR_INPUT, 6));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
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
            ResultSet rs = db.consultar(sql.toString(), parametros.isEmpty() ? null : parametros.toArray());
            tableModel.setRowCount(0);

            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("nombre_completo"),
                    rs.getString("cedula"),
                    rs.getString("telefono"),
                    ""
                });
            }
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudieron cargar los representantes: " + ex.getMessage());
        }
    }

    private void editarRepresentante(String cedula) {
        RepresentanteData data = obtenerRepresentantePorCedula(cedula);
        if (data == null) {
            mostrarDialogoError("No se encontró el representante seleccionado.");
            return;
        }

        abrirFormularioRepresentante(true, data);
    }

    private void cambiarEstadoRepresentante(String cedula) {
        boolean confirmar = mostrarDialogoConfirmacion(
            "Sistema Garita - Confirmar acción",
            "¿Desea eliminar el representante " + cedula + "? Se desactivará permanentemente.",
            ThemeManager.COLOR_ERROR,
            "Eliminar",
            "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            db.comandoDML(
                "UPDATE representantes SET activo = false WHERE cedula = ?",
                new Object[]{cedula}
            );
            cargarRepresentantes();
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudo desactivar el representante: " + ex.getMessage());
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

    private void abrirFormularioRepresentante(boolean esEdicion, RepresentanteData dataInicial) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        final JDialog dialogo;
        if (owner instanceof Frame) {
            dialogo = new JDialog((Frame) owner, true);
        } else if (owner instanceof Dialog) {
            dialogo = new JDialog((Dialog) owner, true);
        } else {
            dialogo = new JDialog();
            dialogo.setModal(true);
        }

        dialogo.setTitle("Sistema Garita - Agregar/Actualizar Representante");
        dialogo.setUndecorated(true);
        dialogo.setSize(470, 330);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JButton btnRegresar = new JButton("←");
        btnRegresar.setFont(new Font("Dialog", Font.BOLD, 18));
        btnRegresar.setForeground(ThemeManager.COLOR_TEXT);
        btnRegresar.setBackground(ThemeManager.COLOR_PRIMARY);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setMargin(new Insets(0, 0, 0, 0));
        btnRegresar.addActionListener(e -> dialogo.dispose());

        JLabel titulo = new JLabel("AGREGAR/ACTUALIZAR REPRESENTANTE", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);

        encabezado.add(btnRegresar, BorderLayout.WEST);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);

        //Selector de vivienda
        //Combobox creado con crearComboViviendas()
        //Se llena con obtenerViviendasActivas() y comboVivienda.addItem()
        //Al guardar se usa comboVivienda.getSelectedItem() para obtener la vivienda asociada
        JLabel lblVivienda = etiquetaDialogo("Vivienda");
        JComboBox<ViviendaItem> comboVivienda = crearComboViviendas();
        comboVivienda.setPreferredSize(new Dimension(260, 30));

        JLabel lblNombre = etiquetaDialogo("Nombre");
        JTextField txtNombreLocal = campoDialogo(dataInicial == null ? "" : dataInicial.nombre);
        txtNombreLocal.setPreferredSize(new Dimension(250, 30));

        JLabel lblApellido = etiquetaDialogo("Apellido");
        JTextField txtApellidoLocal = campoDialogo(dataInicial == null ? "" : dataInicial.apellido);
        txtApellidoLocal.setPreferredSize(new Dimension(250, 30));

        JLabel lblCedula = etiquetaDialogo("Cédula");
        JTextField txtCedulaLocal = campoDialogo(dataInicial == null ? "" : dataInicial.cedula);
        txtCedulaLocal.setPreferredSize(new Dimension(180, 30));

        JLabel lblTelefono = etiquetaDialogo("Teléfono");
        JTextField txtTelefonoLocal = campoDialogo(dataInicial == null ? "" : dataInicial.telefono);
        txtTelefonoLocal.setPreferredSize(new Dimension(180, 30));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(lblVivienda, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(comboVivienda, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtNombreLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblApellido, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtApellidoLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblCedula, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtCedulaLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtTelefonoLocal, gbc);

        List<ViviendaItem> viviendas = obtenerViviendasActivas();
        if (viviendas.isEmpty()) {
            mostrarDialogoError("No hay viviendas activas disponibles para asociar el representante.");
            dialogo.dispose();
            return;
        }

        for (ViviendaItem vivienda : viviendas) {
            comboVivienda.addItem(vivienda);
        }

        if (dataInicial != null) {
            seleccionarVivienda(comboVivienda, dataInicial.idVivienda);
        }

        JButton btnGuardar = ThemeManager.Button(esEdicion ? "Actualizar Representante" : "Agregar Representante");
        btnGuardar.setPreferredSize(new Dimension(260, 36));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnGuardar.addActionListener(e -> {
            ViviendaItem viviendaSeleccionada = (ViviendaItem) comboVivienda.getSelectedItem();
            String nombre = txtNombreLocal.getText().trim();
            String apellido = txtApellidoLocal.getText().trim();
            String cedula = txtCedulaLocal.getText().trim();
            String telefono = txtTelefonoLocal.getText().trim();

            if (viviendaSeleccionada == null) {
                mostrarDialogoError("Debe seleccionar una vivienda.");
                return;
            }

            if (!validarNombre(nombre)) {
                mostrarDialogoError("Nombre inválido.");
                return;
            }

            if (!validarApellido(apellido)) {
                mostrarDialogoError("Apellido inválido.");
                return;
            }

            if (!validarCedula(cedula)) {
                mostrarDialogoError("Cédula inválida.");
                return;
            }

            if (!validarTelefono(telefono)) {
                mostrarDialogoError("Teléfono inválido.");
                return;
            }

            try {
                if (esEdicion) {
                    db.comandoDML(
                        "UPDATE representantes SET id_vivienda = ?, nombre = ?, apellido = ?, cedula = ?, telefono = ? WHERE cedula = ?",
                        new Object[]{viviendaSeleccionada.id, nombre, apellido, cedula, telefono, dataInicial.cedula}
                    );
                } else {
                    ResultSet rsExiste = db.consultar(
                        "SELECT activo FROM representantes WHERE cedula = ?",
                        new Object[]{cedula}
                    );

                    if (rsExiste != null && rsExiste.next()) {
                        boolean estaActivo = rsExiste.getBoolean("activo");
                        if (estaActivo) {
                            mostrarDialogoError("Ya existe un representante con esa cédula.");
                            return;
                        }

                        db.comandoDML(
                            "UPDATE representantes SET id_vivienda = ?, nombre = ?, apellido = ?, telefono = ?, activo = true WHERE cedula = ?",
                            new Object[]{viviendaSeleccionada.id, nombre, apellido, telefono, cedula}
                        );
                    } else {
                        db.comandoDML(
                            "INSERT INTO representantes (id_vivienda, nombre, apellido, cedula, telefono, activo) VALUES (?, ?, ?, ?, ?, true)",
                            new Object[]{viviendaSeleccionada.id, nombre, apellido, cedula, telefono}
                        );
                    }
                }

                dialogo.dispose();
                cargarRepresentantes();
                mostrarDialogoExito("Representante creado/actualizado correctamente.");
            } catch (SQLException ex) {
                mostrarDialogoError("No se pudo guardar el representante: " + ex.getMessage());
            }
        });

        contenido.add(formPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        btnGuardar.setPreferredSize(new Dimension(230, 36));
        btnGuardar.setMaximumSize(new Dimension(230, 36));
        bottom.add(btnGuardar, BorderLayout.CENTER);

        contenido.add(bottom, BorderLayout.SOUTH);

        dialogo.add(encabezado, BorderLayout.NORTH);
        dialogo.add(contenido, BorderLayout.CENTER);
        dialogo.setVisible(true);
    }

    private List<ViviendaItem> obtenerViviendasActivas() {
        List<ViviendaItem> viviendas = new ArrayList<>();

        try {
            ResultSet rs = db.consultar(
                "SELECT id, calle, numero_vivienda FROM viviendas WHERE activo = true ORDER BY numero_vivienda",
                null
            );

            while (rs != null && rs.next()) {
                viviendas.add(new ViviendaItem(
                    rs.getInt("id"),
                    rs.getString("numero_vivienda") + " - " + rs.getString("calle")
                ));
            }
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudieron cargar las viviendas activas: " + ex.getMessage());
        }

        return viviendas;
    }

    private JComboBox<ViviendaItem> crearComboViviendas() {
        JComboBox<ViviendaItem> combo = new JComboBox<>();
        combo.setFont(ThemeManager.TEXT_NORMAL);
        combo.setForeground(ThemeManager.COLOR_TEXT_DARK);
        combo.setBackground(ThemeManager.COLOR_INPUT);
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.COLOR_INPUT, 1));
        return combo;
    }

    private void seleccionarVivienda(JComboBox<ViviendaItem> combo, int idVivienda) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ViviendaItem item = combo.getItemAt(i);
            if (item.id == idVivienda) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private RepresentanteData obtenerRepresentantePorCedula(String cedula) {
        try {
            ResultSet rs = db.consultar(
                "SELECT id_vivienda, nombre, apellido, cedula, telefono FROM representantes WHERE cedula = ? LIMIT 1",
                new Object[]{cedula}
            );

            if (rs != null && rs.next()) {
                return new RepresentanteData(
                    rs.getInt("id_vivienda"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("cedula"),
                    rs.getString("telefono")
                );
            }
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudo cargar el representante: " + ex.getMessage());
        }

        return null;
    }

    private JLabel etiquetaDialogo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(ThemeManager.COLOR_TEXT);
        label.setFont(ThemeManager.TEXT_NORMAL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField campoDialogo(String valorInicial) {
        JTextField field = new JTextField(valorInicial);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        field.setPreferredSize(new Dimension(280, 28));
        field.setBackground(ThemeManager.COLOR_INPUT);
        field.setForeground(ThemeManager.COLOR_TEXT_DARK);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private boolean validarNombre(String nombre) {
        return !nombre.isEmpty() && nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ'\\- ]{2,20}$");
    }

    private boolean validarApellido(String apellido) {
        return !apellido.isEmpty() && apellido.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ'\\- ]{2,20}$");
    }

    private boolean validarCedula(String cedula) {
        return !cedula.isEmpty() && cedula.matches("^[A-Za-z0-9-]{5,13}$");
    }

    private boolean validarTelefono(String telefono) {
        return !telefono.isEmpty() && telefono.matches("^[A-Za-z0-9-]{7,13}$");
    }

    private void mostrarDialogoError(String mensaje) {
        mostrarDialogoEstado("Sistema Garita - ERROR X", mensaje, ThemeManager.COLOR_ERROR, "Aceptar", false);
    }

    private void mostrarDialogoExito(String mensaje) {
        mostrarDialogoEstado("Sistema Garita", mensaje, ThemeManager.COLOR_PRIMARY, "Aceptar", true);
    }

    private boolean mostrarDialogoConfirmacion(String tituloVentana, String mensaje, Color acento, String textoAceptar, String textoCancelar) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        final JDialog dialogo;
        if (owner instanceof Frame) {
            dialogo = new JDialog((Frame) owner, true);
        } else if (owner instanceof Dialog) {
            dialogo = new JDialog((Dialog) owner, true);
        } else {
            dialogo = new JDialog();
            dialogo.setModal(true);
        }

        dialogo.setUndecorated(true);
        dialogo.setTitle(tituloVentana);
        dialogo.setSize(420, 180);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(35, 35, 35));
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(25, 25, 25));
        barra.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JLabel titulo = new JLabel(tituloVentana);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(ThemeManager.TEXT_SMALL);
        barra.add(titulo, BorderLayout.WEST);

        JButton cerrar = new JButton("X");
        cerrar.setForeground(Color.WHITE);
        cerrar.setBackground(new Color(45, 45, 45));
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.setPreferredSize(new Dimension(24, 24));
        cerrar.setContentAreaFilled(false);
        cerrar.addActionListener(e -> dialogo.dispose());
        barra.add(cerrar, BorderLayout.EAST);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(new Color(35, 35, 35));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel mensajePanel = new JPanel(new BorderLayout(10, 0));
        mensajePanel.setBackground(new Color(35, 35, 35));

        JLabel icono = new JLabel("?", SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(28, 28));
        icono.setOpaque(true);
        icono.setBackground(acento);
        icono.setForeground(Color.WHITE);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel mensajeLabel = new JLabel("<html><div style='text-align:center;'>" + mensaje + "</div></html>", SwingConstants.CENTER);
        mensajeLabel.setForeground(Color.WHITE);
        mensajeLabel.setFont(ThemeManager.TEXT_NORMAL);
        mensajeLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        mensajePanel.add(icono, BorderLayout.WEST);
        mensajePanel.add(mensajeLabel, BorderLayout.CENTER);

        JButton aceptar = ThemeManager.Button(textoAceptar);
        aceptar.setMaximumSize(new Dimension(110, 30));
        aceptar.setPreferredSize(new Dimension(110, 30));

        JButton cancelar = new JButton(textoCancelar);
        cancelar.setFont(ThemeManager.TEXT_SMALL);
        cancelar.setForeground(Color.WHITE);
        cancelar.setBackground(new Color(65, 65, 65));
        cancelar.setFocusPainted(false);
        cancelar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        cancelar.addActionListener(e -> dialogo.dispose());

        final boolean[] resultado = {false};
        aceptar.addActionListener(e -> {
            resultado[0] = true;
            dialogo.dispose();
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pie.setBackground(new Color(35, 35, 35));
        pie.add(cancelar);
        pie.add(aceptar);

        cuerpo.add(mensajePanel, BorderLayout.CENTER);
        cuerpo.add(pie, BorderLayout.SOUTH);

        contenedor.add(barra, BorderLayout.NORTH);
        contenedor.add(cuerpo, BorderLayout.CENTER);

        dialogo.add(contenedor, BorderLayout.CENTER);
        dialogo.setVisible(true);
        return resultado[0];
    }

    private void mostrarDialogoEstado(String tituloVentana, String mensaje, Color acento, String textoBoton, boolean exito) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        final JDialog dialogo;
        if (owner instanceof Frame) {
            dialogo = new JDialog((Frame) owner, true);
        } else if (owner instanceof Dialog) {
            dialogo = new JDialog((Dialog) owner, true);
        } else {
            dialogo = new JDialog();
            dialogo.setModal(true);
        }

        dialogo.setUndecorated(true);
        dialogo.setTitle(tituloVentana);
        dialogo.setSize(420, 180);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(35, 35, 35));
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(25, 25, 25));
        barra.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JLabel lblTitulo = new JLabel(tituloVentana);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(ThemeManager.TEXT_SMALL);

        JButton cerrar = new JButton("X");
        cerrar.setForeground(Color.WHITE);
        cerrar.setBackground(new Color(45, 45, 45));
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.setPreferredSize(new Dimension(24, 24));
        cerrar.addActionListener(e -> dialogo.dispose());

        barra.add(lblTitulo, BorderLayout.WEST);
        barra.add(cerrar, BorderLayout.EAST);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(new Color(35, 35, 35));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel mensajePanel = new JPanel(new BorderLayout(10, 0));
        mensajePanel.setBackground(new Color(35, 35, 35));

        JLabel icono = new JLabel(exito ? "i" : "x", SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(28, 28));
        icono.setOpaque(true);
        icono.setBackground(acento);
        icono.setForeground(Color.WHITE);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel texto = new JLabel(mensaje);
        texto.setForeground(Color.WHITE);
        texto.setFont(ThemeManager.TEXT_NORMAL);
        texto.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        mensajePanel.add(icono, BorderLayout.WEST);
        mensajePanel.add(texto, BorderLayout.CENTER);

        JButton aceptar = ThemeManager.Button(textoBoton);
        aceptar.setMaximumSize(new Dimension(100, 30));
        aceptar.setPreferredSize(new Dimension(100, 30));
        aceptar.addActionListener(e -> dialogo.dispose());

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.add(aceptar);

        cuerpo.add(mensajePanel, BorderLayout.CENTER);
        cuerpo.add(pie, BorderLayout.SOUTH);

        contenedor.add(barra, BorderLayout.NORTH);
        contenedor.add(cuerpo, BorderLayout.CENTER);

        dialogo.add(contenedor, BorderLayout.CENTER);
        dialogo.setVisible(true);
    }

    private static class ViviendaItem {
        private final int id;
        private final String descripcion;

        private ViviendaItem(int id, String descripcion) {
            this.id = id;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    private static class RepresentanteData {
        private final int idVivienda;
        private final String nombre;
        private final String apellido;
        private final String cedula;
        private final String telefono;

        private RepresentanteData(int idVivienda, String nombre, String apellido, String cedula, String telefono) {
            this.idVivienda = idVivienda;
            this.nombre = nombre;
            this.apellido = apellido;
            this.cedula = cedula;
            this.telefono = telefono;
        }
    }
}