package Frontend.Residencia;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import Backend.ConexionPostgres;
import Backend.ThemeManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

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

    private static class CuotaPendiente {
        private final int id;
        private final String descripcion;
        private final BigDecimal monto;
        private final Timestamp fechaEmision;
        private final Timestamp fechaLimite;

        private CuotaPendiente(int id, String descripcion, BigDecimal monto, Timestamp fechaEmision, Timestamp fechaLimite) {
            this.id = id;
            this.descripcion = descripcion;
            this.monto = monto;
            this.fechaEmision = fechaEmision;
            this.fechaLimite = fechaLimite;
        }
    }

    private static class DatosConstancia {
        private final String nombreCompleto;
        private final String cedula;

        private DatosConstancia(String nombreCompleto, String cedula) {
            this.nombreCompleto = nombreCompleto;
            this.cedula = cedula;
        }
    }

    public MenuVivienda() {
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        txtNum = ThemeManager.Textfield();

        JPanel pInputNum = new JPanel(new BorderLayout(10, 0));
        pInputNum.setOpaque(false);
        pInputNum.setAlignmentX(Component.LEFT_ALIGNMENT);
        pInputNum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pInputNum.add(lNum, BorderLayout.WEST);
        pInputNum.add(txtNum, BorderLayout.CENTER);

        JLabel lCalle = new JLabel("Calle");
        lCalle.setFont(ThemeManager.TEXT_NORMAL);
        lCalle.setForeground(ThemeManager.COLOR_TEXT);

        txtCalle = ThemeManager.Textfield();

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

    private JLabel crearEstado(String texto, boolean activo) {
        JLabel label = ThemeManager.Label(texto);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(ThemeManager.TEXT_NORMAL);
        if ("Solvente".equalsIgnoreCase(texto)) {
            label.setForeground(new Color(129, 199, 132));
            label.setBackground(new Color(46, 125, 50, 40));
        } else if ("Moroso".equalsIgnoreCase(texto)) {
            label.setForeground(new Color(240, 128, 128));
            label.setBackground(new Color(198, 40, 40, 40));
        } else {
            label.setForeground(ThemeManager.COLOR_TEXT_DARK);
            label.setBackground(activo ? ThemeManager.COLOR_SECONDARY : ThemeManager.COLOR_ERROR);
        }
        label.setFont(ThemeManager.TEXT_NORMAL);
        label.setPreferredSize(new Dimension(82, 22));
        label.setMinimumSize(new Dimension(82, 22));
        label.setMaximumSize(new Dimension(82, 22));
        return label;
    }

    private JPanel crearAccionesVivienda(ViviendaItem vivienda) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panel.setOpaque(false);

        JButton btnPagar = crearBotonIcono("img\\dolar.png");
        JButton btnVerDeudas = crearBotonIcono("img\\tabla-de-crecimiento.png");
        JButton btnEditar = crearBotonIcono("img\\edit.png");
        JButton btnEliminar = crearBotonIcono("img\\delete.png");

        btnPagar.addActionListener(e -> abrirVentanaPagoCuota(vivienda));
        btnVerDeudas.addActionListener(e -> mostrarCuotasPendientes(vivienda));
        btnEditar.addActionListener(e -> editarVivienda(vivienda.numero, vivienda.calle, vivienda.activo));
        btnEliminar.addActionListener(e -> cambiarEstadoVivienda(vivienda.numero, vivienda.activo));

        panel.add(btnPagar);
        panel.add(btnVerDeudas);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        return panel;
    }

    private void abrirVentanaPagoCuota(ViviendaItem vivienda) {
        CuotaPendiente cuota = obtenerCuotaActivaPendiente(vivienda.id);
        if (cuota == null) {
            mostrarDialogoError("No hay cuota activa pendiente para esta vivienda.");
            return;
        }

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
        dialogo.setTitle("Sistema Garita - Pagar Cuota");
        dialogo.setSize(430, 290);
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

        JLabel titulo = new JLabel("PAGAR CUOTA", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);

        encabezado.add(btnRegresar, BorderLayout.WEST);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblCuota = new JLabel(cuota.descripcion.toUpperCase(), SwingConstants.CENTER);
        lblCuota.setForeground(ThemeManager.COLOR_TEXT);
        lblCuota.setFont(ThemeManager.TEXT_SUBTITLE);

        JLabel lblMonto = new JLabel("$ " + cuota.monto.toPlainString(), SwingConstants.CENTER);
        lblMonto.setForeground(ThemeManager.COLOR_TEXT);
        lblMonto.setFont(new Font("Verdana", Font.BOLD, 24));

        JLabel lblTipoPago = etiquetaDialogo("Tipo de Pago");
        JComboBox<String> comboTipoPago = new JComboBox<>(new String[]{"Pago Móvil", "Transferencia", "Efectivo"});
        comboTipoPago.setFont(ThemeManager.TEXT_NORMAL);
        comboTipoPago.setBackground(ThemeManager.COLOR_INPUT);
        comboTipoPago.setForeground(ThemeManager.COLOR_TEXT_DARK);

        JLabel lblReferencia = etiquetaDialogo("Referencia (últimos 4 dígitos)");
        JTextField txtReferencia = campoDialogo("");
        restringirSoloNumeros(txtReferencia, 4);

        JButton btnPagar = ThemeManager.Button("Pagar Cuota");
        btnPagar.setPreferredSize(new Dimension(280, 38));
        btnPagar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnPagar.addActionListener(e -> {
            String referencia = txtReferencia.getText().trim();
            String tipoPago = comboTipoPago.getSelectedItem().toString();

            if (!referencia.matches("^\\d{4}$")) {
                mostrarDialogoError("Número de Referencia Inválido.");
                return;
            }

            try {
                ResultSet yaPagada = ConexionPostgres.consultar(
                    "SELECT 1 FROM pagos_realizados WHERE id_vivienda = ? AND id_cuota = ? LIMIT 1",
                    new Object[]{vivienda.id, cuota.id}
                );

                if (yaPagada != null && yaPagada.next()) {
                    mostrarDialogoError("La cuota activa ya fue pagada para esta vivienda.");
                    return;
                }

                ConexionPostgres.comandoDML(
                    "INSERT INTO pagos_realizados (id_vivienda, id_cuota, tipo_pago, referencia, fecha_de_pago) VALUES (?, ?, ?, ?, NOW())",
                    new Object[]{vivienda.id, cuota.id, tipoPago, referencia}
                );

                generarReciboPagoPDF(vivienda, cuota, tipoPago, referencia);
                dialogo.dispose();
                mostrarDialogoExito("Pago registrado correctamente.");
            } catch (SQLException ex) {
                mostrarDialogoError("No se pudo registrar el pago: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        contenido.add(lblCuota, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 12, 5);
        contenido.add(lblMonto, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(7, 5, 3, 5);
        contenido.add(lblTipoPago, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 5, 7, 5);
        contenido.add(comboTipoPago, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(7, 5, 3, 5);
        contenido.add(lblReferencia, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 5, 12, 5);
        contenido.add(txtReferencia, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(8, 5, 0, 5);
        contenido.add(btnPagar, gbc);

        dialogo.add(encabezado, BorderLayout.NORTH);
        dialogo.add(contenido, BorderLayout.CENTER);
        dialogo.setVisible(true);
    }

    private CuotaPendiente obtenerCuotaActivaPendiente(int idVivienda) {
        try {
            ResultSet rs = ConexionPostgres.consultar(
                "SELECT c.id, c.descripcion, c.monto, c.fecha_emision, c.fecha_limite " +
                "FROM cuotas c " +
                "WHERE c.activo = true " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM pagos_realizados pr WHERE pr.id_cuota = c.id AND pr.id_vivienda = ?" +
                ") " +
                "ORDER BY c.fecha_emision ASC, c.id ASC " +
                "LIMIT 1",
                new Object[]{idVivienda}
            );

            if (rs != null && rs.next()) {
                return new CuotaPendiente(
                    rs.getInt("id"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("monto"),
                    rs.getTimestamp("fecha_emision"),
                    rs.getTimestamp("fecha_limite")
                );
            }
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudo consultar la cuota activa: " + ex.getMessage());
        }

        return null;
    }

    private ArrayList<CuotaPendiente> obtenerCuotasPendientesVivienda(int idVivienda) {
        ArrayList<CuotaPendiente> pendientes = new ArrayList<>();
        try {
            ResultSet rs = ConexionPostgres.consultar(
                "SELECT c.id, c.descripcion, c.monto, c.fecha_emision, c.fecha_limite " +
                "FROM cuotas c " +
                "WHERE c.activo = true " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM pagos_realizados pr WHERE pr.id_cuota = c.id AND pr.id_vivienda = ?" +
                ") " +
                "ORDER BY c.fecha_emision ASC, c.id ASC",
                new Object[]{idVivienda}
            );

            while (rs != null && rs.next()) {
                pendientes.add(new CuotaPendiente(
                    rs.getInt("id"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("monto"),
                    rs.getTimestamp("fecha_emision"),
                    rs.getTimestamp("fecha_limite")
                ));
            }
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudieron consultar las cuotas pendientes: " + ex.getMessage());
        }

        return pendientes;
    }

    private void mostrarCuotasPendientes(ViviendaItem vivienda) {
        ArrayList<CuotaPendiente> pendientes = obtenerCuotasPendientesVivienda(vivienda.id);
        DatosConstancia datos;
        try {
            datos = obtenerDatosConstancia(vivienda.id);
        } catch (SQLException ex) {
            mostrarDialogoError("No se pudieron cargar los datos de la vivienda: " + ex.getMessage());
            return;
        }

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
        dialogo.setTitle("Sistema Garita - Cuotas Pendientes");
        dialogo.setSize(760, 440);
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

        JLabel titulo = new JLabel("CUOTAS PENDIENTES", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);

        encabezado.add(btnRegresar, BorderLayout.WEST);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 10, 6);

        JPanel panelDatos = new JPanel(new GridLayout(2, 2, 8, 8));
        panelDatos.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDatos.add(crearEtiquetaInfo("Vivienda", vivienda.numero + " - " + vivienda.calle));
        panelDatos.add(crearEtiquetaInfo("Representante", datos.nombreCompleto));
        panelDatos.add(crearEtiquetaInfo("Cédula", datos.cedula));
        panelDatos.add(crearEtiquetaInfo("Estado", tieneCuotaVencida(pendientes) ? "Moroso" : "Solvente"));

        String[] columnas = {"Cuota", "Monto", "Fecha Emisión", "Fecha Límite"};
        DefaultTableModel modeloPendientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat fechaFormato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (CuotaPendiente cuota : pendientes) {
            modeloPendientes.addRow(new Object[]{
                cuota.descripcion,
                "$ " + cuota.monto.toPlainString(),
                fechaFormato.format(cuota.fechaEmision),
                fechaFormato.format(cuota.fechaLimite)
            });
        }

        JTable tabla = new JTable(modeloPendientes);
        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(ThemeManager.TEXT_NORMAL);
        tabla.getTableHeader().setFont(ThemeManager.TEXT_SMALL);
        tabla.getTableHeader().setBackground(ThemeManager.COLOR_PRIMARY);
        tabla.getTableHeader().setForeground(ThemeManager.COLOR_TEXT);
        tabla.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        tabla.setForeground(ThemeManager.COLOR_TEXT);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel tituloTabla = new JLabel(pendientes.isEmpty() ? "No tiene cuotas pendientes" : "Detalle de cuotas pendientes");
        tituloTabla.setForeground(ThemeManager.COLOR_TEXT);
        tituloTabla.setFont(ThemeManager.TEXT_SUBTITLE);

        gbc.gridy = 0;
        contenido.add(panelDatos, gbc);
        gbc.gridy = 1;
        contenido.add(tituloTabla, gbc);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contenido.add(scroll, gbc);

        dialogo.add(encabezado, BorderLayout.NORTH);
        dialogo.add(contenido, BorderLayout.CENTER);
        dialogo.setVisible(true);
    }

    private JLabel crearEtiquetaInfo(String titulo, String valor) {
        JLabel label = new JLabel("<html><b>" + titulo + ":</b> " + valor + "</html>");
        label.setForeground(ThemeManager.COLOR_TEXT);
        label.setFont(ThemeManager.TEXT_NORMAL);
        return label;
    }

    /**
     * Una vivienda es "Moroso" solo si tiene al menos una cuota pendiente cuya
     * fecha límite ya pasó. Una cuota pendiente que todavía está dentro de su
     * plazo (fecha límite futura) no cuenta como morosidad.
     */
    private boolean tieneCuotaVencida(List<CuotaPendiente> pendientes) {
        java.util.Date ahora = new java.util.Date();
        for (CuotaPendiente cuota : pendientes) {
            if (cuota.fechaLimite != null && cuota.fechaLimite.before(ahora)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Entre las cuotas pendientes, devuelve la vencida más antigua (la que
     * realmente origina la morosidad). Si no hay ninguna vencida, devuelve null.
     */
    private CuotaPendiente obtenerCuotaVencidaMasAntigua(List<CuotaPendiente> pendientes) {
        java.util.Date ahora = new java.util.Date();
        CuotaPendiente masAntigua = null;
        for (CuotaPendiente cuota : pendientes) {
            if (cuota.fechaLimite == null || !cuota.fechaLimite.before(ahora)) {
                continue;
            }
            if (masAntigua == null || cuota.fechaLimite.before(masAntigua.fechaLimite)) {
                masAntigua = cuota;
            }
        }
        return masAntigua;
    }

    private DatosConstancia obtenerDatosConstancia(int idVivienda) throws SQLException {
        ResultSet rs = ConexionPostgres.consultar(
            "SELECT r.nombre, r.apellido, r.cedula " +
            "FROM viviendas v " +
            "LEFT JOIN representantes r ON r.id_vivienda = v.id AND r.activo = true " +
            "WHERE v.id = ? " +
            "ORDER BY r.id " +
            "LIMIT 1",
            new Object[]{idVivienda}
        );

        if (rs != null && rs.next()) {
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            String cedula = rs.getString("cedula");
            String nombreCompleto = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();

            if (nombreCompleto.isEmpty()) {
                nombreCompleto = "SIN REPRESENTANTE REGISTRADO";
            }
            if (cedula == null || cedula.trim().isEmpty()) {
                cedula = "NO REGISTRADA";
            }

            return new DatosConstancia(nombreCompleto, cedula);
        }

        return new DatosConstancia("SIN REPRESENTANTE REGISTRADO", "NO REGISTRADA");
    }

    private void generarReciboPagoPDF(ViviendaItem vivienda, CuotaPendiente cuota, String tipoPago, String referencia) {
        Document documento = new Document();
        try {
            DatosConstancia datos = obtenerDatosConstancia(vivienda.id);
            String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

            ArrayList<CuotaPendiente> pendientesActuales = obtenerCuotasPendientesVivienda(vivienda.id);
            boolean esMoroso = tieneCuotaVencida(pendientesActuales);
            String estadoVivienda = esMoroso ? "Moroso" : "Solvente";

            // Si está moroso, el mes/año de referencia debe ser el de la cuota vencida
            // más antigua (la que realmente origina la morosidad), no el de la cuota
            // que se acaba de pagar en esta transacción. Si está solvente, se usa la
            // cuota recién pagada como referencia de "al día hasta".
            CuotaPendiente cuotaVencidaMasAntigua = esMoroso ? obtenerCuotaVencidaMasAntigua(pendientesActuales) : null;
            java.sql.Timestamp fechaReferencia = cuotaVencidaMasAntigua != null ? cuotaVencidaMasAntigua.fechaLimite : cuota.fechaLimite;

            String anioCuota = new SimpleDateFormat("yyyy").format(fechaReferencia);
            String mesCuota = new SimpleDateFormat("MMMM", new Locale("es", "VE")).format(fechaReferencia).toUpperCase();

            String nombreArchivo = "Constancia_Solvencia_" + vivienda.numero + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf";
            File carpetaFacturas = new File("Garita" + File.separator + "facturas");
            if (!carpetaFacturas.exists()) {
                carpetaFacturas.mkdirs();
            }
            File archivoPdf = new File(carpetaFacturas, nombreArchivo);

            PdfWriter.getInstance(documento, new FileOutputStream(archivoPdf));
            documento.open();

            Paragraph encabezado = new Paragraph(
                "REPUBLICA BOLIVARIANA DE VENEZUELA\n" +
                "MUNICIPIO MARACAIBO - PARROQUIA RAUL LEONI\n" +
                "ASOCIACION DE PROPIETARIOS Y VECINOS DE LA \"URB. SANTA FE III ETAPA\"\n" +
                "Rif: J29613737-4"
            );
            encabezado.setSpacingAfter(12);
            documento.add(encabezado);

            Paragraph titulo = new Paragraph("Constancia de Solvencia");
            titulo.setAlignment(Element.ALIGN_LEFT);
            titulo.setSpacingAfter(14);
            documento.add(titulo);

            Paragraph cuerpo = new Paragraph(
                "Quienes Suscribimos miembros de la Junta Directiva de la Asociación de Propietarios y Vecinos de la \"Urb. Santa Fe III Etapa\", de la parroquia Raúl Leoni, Municipio Maracaibo, Estado Zulia, por medio de la presente\n\n" +
                "Hacemos constar que el ciudadano(a): " + datos.nombreCompleto + ", de la cédula " + datos.cedula + " " +
                "propietario en la calle " + vivienda.calle + " Casa N° " + vivienda.numero + " se encuentra " + estadoVivienda + " " +
                "con las Cuotas ordinaria y/o Extraordinaria de Mantenimiento de la Asociación y servicios Municipales (Aseo y Gas) " +
                "SEDEMAT año " + anioCuota + " HASTA EL DE " + mesCuota + ".\n\n" +
                "Constancia que se expide a petición de la parte interesada en Maracaibo a los " + fechaActual + "\n\n" +
                "Atentamente\n" +
                "Por la Junta Directiva"
            );
            cuerpo.setSpacingAfter(18);
            documento.add(cuerpo);

            PdfPTable tablaPago = new PdfPTable(2);
            tablaPago.setWidthPercentage(100);
            tablaPago.setSpacingBefore(8);
            tablaPago.setWidths(new float[]{1.1f, 2.4f});

            agregarCeldaInfo(tablaPago, "Cuota:", cuota.descripcion.toUpperCase());
            agregarCeldaInfo(tablaPago, "Monto:", "$ " + cuota.monto.toPlainString());
            agregarCeldaInfo(tablaPago, "Fecha Emisión Cuota:", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cuota.fechaEmision));
            agregarCeldaInfo(tablaPago, "Tipo de Pago:", tipoPago);
            agregarCeldaInfo(tablaPago, "Referencia:", referencia);
            agregarCeldaInfo(tablaPago, "Fecha de Pago:", fechaActual);
            agregarCeldaInfo(tablaPago, "Fecha Límite Cuota:", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cuota.fechaLimite));

            documento.add(tablaPago);
            
            Paragraph piePagina = new Paragraph("Av. 84 URB. SANTA FE III ETAPA, PARROQUIA RAÚL LEONI, MUNICIPIO MARACAIBO - EDO. ZULIA Teléfono: 0412-7512230 / 0412-0794503");
            piePagina.setSpacingBefore(16);
            documento.add(piePagina);
            
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(archivoPdf);
                } catch (Exception ignored) {
                }
            }
        } catch (DocumentException | FileNotFoundException | SQLException ex) {
            mostrarDialogoError("Pago registrado, pero no se pudo generar el PDF: " + ex.getMessage());
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    private void agregarCeldaInfo(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta));
        celdaEtiqueta.setBackgroundColor(ThemeManager.COLOR_SECONDARY);
        celdaEtiqueta.setPadding(6);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor));
        celdaValor.setPadding(6);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
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
        boolean confirmar = mostrarDialogoConfirmacionEliminacion(
            "Sistema Garita - Eliminar Vivienda",
            "¿Desea eliminar la vivienda " + numeroVivienda + "? Se eliminará permanentemente."
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
            mostrarDialogoExitoEliminacion("La vivienda ha sido eliminada con éxito.");
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

        // Restricciones de escritura: solo se puede teclear lo que tiene sentido para cada campo
        restringirCalle(txtCalleLocal, 30);
        restringirNumeroVivienda(txtNumeroLocal, 10);

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

            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";
            try {
                if (esEdicion) {

                    ConexionPostgres.comandoDML(
                        "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE viviendas SET calle = ?, numero_vivienda = ? WHERE numero_vivienda = ?",
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
                            "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE viviendas SET calle = ?, activo = true WHERE numero_vivienda = ?",
                            new Object[]{calle, numero}
                        );
                    } else {
                        ConexionPostgres.comandoDML(
                            "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "INSERT INTO viviendas (calle, numero_vivienda, activo) VALUES (?, ?, true)",
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

    /**
     * Valida "Calle y Avenida": letras (con tildes/ñ), números, espacios, '#'
     * y '-'. Entre 3 y 30 caracteres, sin espacios dobles ni espacios al
     * inicio/final.
     */
    private boolean validarCalle(String calle) {
        if (calle == null || calle.isEmpty()) return false;
        if (calle.length() < 3 || calle.length() > 30) return false;
        if (calle.contains("  ")) return false;
        if (calle.startsWith(" ") || calle.endsWith(" ")) return false;
        return calle.matches("^[A-Za-z0-9áéíóúÁÉÍÓÚñÑ #\\-]{3,30}$");
    }

    /**
     * Valida "Número de Casa": letras, números y guion (ej. "12", "12-A").
     * Entre 1 y 10 caracteres, sin espacios.
     */
    private boolean validarNumero(String numero) {
        if (numero == null || numero.isEmpty()) return false;
        return numero.matches("^[A-Za-z0-9\\-]{1,10}$");
    }

    /**
     * Restringe un JTextField para que solo acepte letras (con tildes/ñ),
     * números, espacios, '#' y '-', respetando un largo máximo.
     */
    private void restringirCalle(JTextField campo, int maxLength) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^A-Za-z0-9áéíóúÁÉÍÓÚñÑ #\\-]", "");
                if (filtrado.isEmpty()) return;
                int espacioDisponible = maxLength - fb.getDocument().getLength();
                if (espacioDisponible <= 0) return;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.insertString(fb, offset, filtrado, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String filtrado = text == null ? "" : text.replaceAll("[^A-Za-z0-9áéíóúÁÉÍÓÚñÑ #\\-]", "");
                int largoActual = fb.getDocument().getLength() - length;
                int espacioDisponible = maxLength - largoActual;
                if (espacioDisponible < 0) espacioDisponible = 0;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.replace(fb, offset, length, filtrado, attrs);
            }
        });
    }

    /**
     * Restringe un JTextField para que, sin importar lo que se pegue o teclee,
     * solo queden dígitos (0-9), respetando un largo máximo.
     */
    private void restringirSoloNumeros(JTextField campo, int maxLength) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^0-9]", "");
                if (filtrado.isEmpty()) return;
                int espacioDisponible = maxLength - fb.getDocument().getLength();
                if (espacioDisponible <= 0) return;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.insertString(fb, offset, filtrado, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String filtrado = text == null ? "" : text.replaceAll("[^0-9]", "");
                int largoActual = fb.getDocument().getLength() - length;
                int espacioDisponible = maxLength - largoActual;
                if (espacioDisponible < 0) espacioDisponible = 0;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.replace(fb, offset, length, filtrado, attrs);
            }
        });
    }

    /**
     * Restringe un JTextField para que solo acepte letras, números y guion,
     * sin espacios, respetando un largo máximo.
     */
    private void restringirNumeroVivienda(JTextField campo, int maxLength) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^A-Za-z0-9\\-]", "");
                if (filtrado.isEmpty()) return;
                int espacioDisponible = maxLength - fb.getDocument().getLength();
                if (espacioDisponible <= 0) return;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.insertString(fb, offset, filtrado, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String filtrado = text == null ? "" : text.replaceAll("[^A-Za-z0-9\\-]", "");
                int largoActual = fb.getDocument().getLength() - length;
                int espacioDisponible = maxLength - largoActual;
                if (espacioDisponible < 0) espacioDisponible = 0;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.replace(fb, offset, length, filtrado, attrs);
            }
        });
    }

    private void mostrarDialogoError(String mensaje) {
        mostrarDialogoEstado("Sistema Garita - ERROR X", mensaje, ThemeManager.COLOR_ERROR, "Aceptar", false);
    }

    private void mostrarDialogoExito(String mensaje) {
        mostrarDialogoEstado("Sistema Garita", mensaje, ThemeManager.COLOR_PRIMARY, "Aceptar", true);
    }

    private boolean mostrarDialogoConfirmacionEliminacion(String tituloVentana, String mensaje) {
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
        dialogo.setSize(460, 180);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_ERROR);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTitulo = new JLabel(tituloVentana);
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setFont(ThemeManager.TEXT_SUBTITLE);

        JButton cerrar = new JButton("←");
        cerrar.setFont(new Font("Dialog", Font.BOLD, 18));
        cerrar.setForeground(ThemeManager.COLOR_TEXT);
        cerrar.setBackground(ThemeManager.COLOR_ERROR);
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.setContentAreaFilled(false);
        cerrar.setMargin(new Insets(0, 0, 0, 0));
        cerrar.addActionListener(e -> dialogo.dispose());

        encabezado.add(cerrar, BorderLayout.WEST);
        encabezado.add(lblTitulo, BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel mensajeLabel = new JLabel("<html><div style='text-align:center;'>" + mensaje + "</div></html>", SwingConstants.CENTER);
        mensajeLabel.setForeground(ThemeManager.COLOR_TEXT);
        mensajeLabel.setFont(ThemeManager.TEXT_NORMAL);

        JPanel iconoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconoPanel.setOpaque(false);
        JLabel icono = new JLabel("!", SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(34, 34));
        icono.setOpaque(true);
        icono.setBackground(ThemeManager.COLOR_ERROR);
        icono.setForeground(ThemeManager.COLOR_TEXT);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));
        iconoPanel.add(icono);

        JPanel mensajePanel = new JPanel(new BorderLayout(10, 0));
        mensajePanel.setOpaque(false);
        mensajePanel.add(iconoPanel, BorderLayout.WEST);
        mensajePanel.add(mensajeLabel, BorderLayout.CENTER);

        JButton cancelar = new JButton("Cancelar");
        cancelar.setFont(ThemeManager.TEXT_SMALL);
        cancelar.setForeground(ThemeManager.COLOR_TEXT);
        cancelar.setBackground(new Color(65, 65, 65));
        cancelar.setFocusPainted(false);
        cancelar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        cancelar.addActionListener(e -> dialogo.dispose());

        JButton aceptar = ThemeManager.Button("Eliminar");
        aceptar.setBackground(ThemeManager.COLOR_ERROR);
        aceptar.setPreferredSize(new Dimension(110, 30));
        aceptar.setMaximumSize(new Dimension(110, 30));

        final boolean[] resultado = {false};
        aceptar.addActionListener(e -> {
            resultado[0] = true;
            dialogo.dispose();
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pie.setOpaque(false);
        pie.add(cancelar);
        pie.add(aceptar);

        cuerpo.add(mensajePanel, BorderLayout.CENTER);
        cuerpo.add(pie, BorderLayout.SOUTH);

        contenedor.add(encabezado, BorderLayout.NORTH);
        contenedor.add(cuerpo, BorderLayout.CENTER);

        dialogo.add(contenedor, BorderLayout.CENTER);
        dialogo.setVisible(true);
        return resultado[0];
    }

    private void mostrarDialogoExitoEliminacion(String mensaje) {
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
        dialogo.setTitle("Sistema Garita - Eliminación Exitosa");
        dialogo.setSize(390, 160);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55), 1));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTitulo = new JLabel("ELIMINACIÓN EXITOSA");
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setFont(ThemeManager.TEXT_SUBTITLE);
        encabezado.add(lblTitulo, BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout(10, 0));
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));

        JLabel icono = new JLabel("✓", SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(34, 34));
        icono.setOpaque(true);
        icono.setBackground(ThemeManager.COLOR_PRIMARY);
        icono.setForeground(ThemeManager.COLOR_TEXT);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel texto = new JLabel(mensaje, SwingConstants.LEFT);
        texto.setForeground(ThemeManager.COLOR_TEXT);
        texto.setFont(ThemeManager.TEXT_NORMAL);

        cuerpo.add(icono, BorderLayout.WEST);
        cuerpo.add(texto, BorderLayout.CENTER);

        JButton aceptar = ThemeManager.Button("Aceptar");
        aceptar.setPreferredSize(new Dimension(110, 30));
        aceptar.setMaximumSize(new Dimension(110, 30));
        aceptar.addActionListener(e -> dialogo.dispose());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pie.setOpaque(false);
        pie.add(aceptar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(cuerpo, BorderLayout.CENTER);
        centro.add(pie, BorderLayout.SOUTH);

        contenedor.add(encabezado, BorderLayout.NORTH);
        contenedor.add(centro, BorderLayout.CENTER);

        dialogo.add(contenedor, BorderLayout.CENTER);
        dialogo.setVisible(true);
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