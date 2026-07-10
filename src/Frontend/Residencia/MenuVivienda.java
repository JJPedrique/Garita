package Frontend.Residencia;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import Backend.ConexionPostgres;
import Backend.ThemeManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        private final String numero;
        private final String calle;
        private final String estado;
        private final boolean activo;

        private ViviendaItem(String numero, String calle, String estado, boolean activo) {
            this.numero = numero;
            this.calle = calle;
            this.estado = estado;
            this.activo = activo;
        }
    }

    public MenuVivienda() {
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(ThemeManager.COLOR_BACKGROUND);
        panelControles.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panelControles.setPreferredSize(new Dimension(315, 0));

        JButton btnAgregar = ThemeManager.Button("Agregar");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAgregar.addActionListener(e -> abrirFormularioVivienda(false, null, null, true));

        JLabel tituloAgregar = new JLabel("<html><div style='text-align:center;'>AGREGAR NUEVA VIVIENDA</div></html>", SwingConstants.CENTER);
        tituloAgregar.setFont(ThemeManager.TEXT_SMALL);
        tituloAgregar.setForeground(ThemeManager.COLOR_TEXT);
        tituloAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JSeparator separador = new JSeparator();
        separador.setForeground(ThemeManager.COLOR_INPUT);
        separador.setBackground(ThemeManager.COLOR_INPUT);
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
        panelFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel lNum = new JLabel("Número de Vivienda");
        lNum.setFont(ThemeManager.TEXT_SMALL);
        lNum.setForeground(ThemeManager.COLOR_TEXT);
        lNum.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNum = ThemeManager.Textfield();
        txtNum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtNum.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lCalle = new JLabel("Calle");
        lCalle.setFont(ThemeManager.TEXT_SMALL);
        lCalle.setForeground(ThemeManager.COLOR_TEXT);
        lCalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCalle = ThemeManager.Textfield();
        txtCalle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtCalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelFiltros.add(lNum);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 6)));
        panelFiltros.add(txtNum);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFiltros.add(lCalle);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 6)));
        panelFiltros.add(txtCalle);

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
            "SELECT numero_vivienda, calle, CASE WHEN activo THEN 'Activo' ELSE 'Inactivo' END AS estado " +
            "FROM viviendas"
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

    private JLabel crearEstado(String texto, boolean activo) {
        JLabel label = ThemeManager.Label(texto);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setForeground(ThemeManager.COLOR_TEXT_DARK);
        label.setBackground(activo ? ThemeManager.COLOR_SECONDARY : ThemeManager.COLOR_ERROR);
        label.setFont(ThemeManager.TEXT_NORMAL);
        label.setPreferredSize(new Dimension(76, 20));
        label.setMinimumSize(new Dimension(76, 20));
        label.setMaximumSize(new Dimension(76, 20));
        return label;
    }

    private JPanel crearAccionesVivienda(ViviendaItem vivienda) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panel.setOpaque(false);

        JButton btnEditar = crearBotonIcono("img\\edit.png");
        JButton btnEliminar = crearBotonIcono("img\\delete.png");

        btnEditar.addActionListener(e -> editarVivienda(vivienda.numero, vivienda.calle, vivienda.activo));
        btnEliminar.addActionListener(e -> cambiarEstadoVivienda(vivienda.numero, vivienda.activo));

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

    private void editarVivienda(String numeroVivienda, String calleActual, boolean activoActual) {
        abrirFormularioVivienda(true, numeroVivienda, calleActual, activoActual);
    }

    private void cambiarEstadoVivienda(String numeroVivienda, boolean activoActual) {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Desea eliminar la vivienda " + numeroVivienda + "? Se eliminará permanentemente.",
            "Confirmar acción",
            JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ConexionPostgres.comandoDML(
                "UPDATE viviendas SET activo = false WHERE numero_vivienda = ?",
                new Object[]{numeroVivienda}
            );
            cargarViviendas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo desactivar la vivienda: " + ex.getMessage());
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

    private void abrirFormularioVivienda(boolean esEdicion, String numeroOriginal, String calleInicial, boolean activoInicial) {
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
        dialogo.setTitle("Sistema Garita - Agregar/Actualizar Vivienda");
        dialogo.setUndecorated(true);
        dialogo.setSize(430, 245);
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

        JLabel titulo = new JLabel("AGREGAR/ACTUALIZAR VIVIENDA", SwingConstants.CENTER);
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

        JLabel lblCalle = etiquetaDialogo("Calle y Avenida");
        JTextField txtCalleLocal = campoDialogo(calleInicial == null ? "" : calleInicial);
        txtCalleLocal.setPreferredSize(new Dimension(250, 30));

        JLabel lblNumero = etiquetaDialogo("Número de Casa");
        JTextField txtNumeroLocal = campoDialogo(numeroOriginal == null ? "" : numeroOriginal);
        txtNumeroLocal.setPreferredSize(new Dimension(160, 30));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(lblCalle, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtCalleLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblNumero, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtNumeroLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 1.0;
        formPanel.add(Box.createVerticalStrut(2), gbc);

        JButton btnGuardar = ThemeManager.Button(esEdicion ? "Actualizar Vivienda" : "Agregar Vivienda");
        btnGuardar.setPreferredSize(new Dimension(260, 36));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnGuardar.addActionListener(e -> {
            String calle = txtCalleLocal.getText().trim();
            String numero = txtNumeroLocal.getText().trim();

            if (!validarCalle(calle)) {
                mostrarDialogoError("Calle y Avenida inválida.");
                return;
            }

            if (!validarNumero(numero)) {
                mostrarDialogoError("Número de Casa inválido.");
                return;
            }

            try {
                if (esEdicion) {
                    ConexionPostgres.comandoDML(
                        "UPDATE viviendas SET calle = ?, numero_vivienda = ? WHERE numero_vivienda = ?",
                        new Object[]{calle, numero, numeroOriginal}
                    );
                } else {
                    ResultSet rsExiste = ConexionPostgres.consultar(
                        "SELECT activo FROM viviendas WHERE numero_vivienda = ?",
                        new Object[]{numero}
                    );

                    if (rsExiste != null && rsExiste.next()) {
                        boolean estaActiva = rsExiste.getBoolean("activo");
                        if (estaActiva) {
                            mostrarDialogoError("Ya existe una vivienda con ese número.");
                            return;
                        }

                        ConexionPostgres.comandoDML(
                            "UPDATE viviendas SET calle = ?, activo = true WHERE numero_vivienda = ?",
                            new Object[]{calle, numero}
                        );
                    } else {
                        ConexionPostgres.comandoDML(
                            "INSERT INTO viviendas (calle, numero_vivienda, activo) VALUES (?, ?, true)",
                            new Object[]{calle, numero}
                        );
                    }
                }

                dialogo.dispose();
                cargarViviendas();
                mostrarDialogoExito("Vivienda creada/actualizada correctamente.");
            } catch (SQLException ex) {
                mostrarDialogoError("No se pudo guardar la vivienda: " + ex.getMessage());
            }
        });

        contenido.add(formPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        btnGuardar.setPreferredSize(new Dimension(210, 36));
        btnGuardar.setMaximumSize(new Dimension(210, 36));
        bottom.add(btnGuardar, BorderLayout.CENTER);

        contenido.add(bottom, BorderLayout.SOUTH);

        dialogo.add(encabezado, BorderLayout.NORTH);
        dialogo.add(contenido, BorderLayout.CENTER);
        dialogo.setVisible(true);
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

    private boolean validarCalle(String calle) {
        return !calle.isEmpty() && calle.matches("^[A-Za-z0-9 áéíóúÁÉÍÓÚ#\\-]{3,30}$");
    }

    private boolean validarNumero(String numero) {
        return !numero.isEmpty() && numero.matches("^[A-Za-z0-9\\-]{1,10}$");
    }

    private void mostrarDialogoError(String mensaje) {
        mostrarDialogoEstado("Sistema Garita - ERROR X", mensaje, ThemeManager.COLOR_ERROR, "Aceptar", false);
    }

    private void mostrarDialogoExito(String mensaje) {
        mostrarDialogoEstado("Sistema Garita", mensaje, ThemeManager.COLOR_PRIMARY, "Aceptar", true);
    }

    private void mostrarDialogoEstado(String tituloVentana, String mensaje, Color acento, String textoBoton, boolean exito) {
        Window owner2 = SwingUtilities.getWindowAncestor(this);
        final JDialog dialogo;
        if (owner2 instanceof Frame) {
            dialogo = new JDialog((Frame) owner2, true);
        } else if (owner2 instanceof Dialog) {
            dialogo = new JDialog((Dialog) owner2, true);
        } else {
            dialogo = new JDialog();
            dialogo.setModal(true);
        }
        dialogo.setUndecorated(true);
        dialogo.setTitle(tituloVentana);
        dialogo.setSize(360, 150);
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
}