package Frontend.Residencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FrameFormularioRepresentante extends JPanel {

    public static class ViviendaComboItem {
        public final int id;
        public final String descripcion;

        public ViviendaComboItem(int id, String descripcion) {
            this.id = id;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }

        public static List<ViviendaComboItem> obtenerActivas() {
            List<ViviendaComboItem> viviendas = new ArrayList<>();
            try {
                ResultSet rs = ConexionPostgres.consultar(
                    "SELECT id, calle, numero_vivienda FROM viviendas WHERE activo = true ORDER BY numero_vivienda",
                    null
                );

                while (rs != null && rs.next()) {
                    viviendas.add(new ViviendaComboItem(
                        rs.getInt("id"),
                        rs.getString("numero_vivienda") + " - " + rs.getString("calle")
                    ));
                }
            } catch (SQLException ex) {
                viviendas = new ArrayList<>();
            }
            return viviendas;
        }
    }

    /** Datos iniciales para edición. */
    public static class RepresentanteData {
        public final int idVivienda;
        public final String nombre;
        public final String apellido;
        public final String cedula;
        public final String telefono;

        public RepresentanteData(int idVivienda, String nombre, String apellido, String cedula, String telefono) {
            this.idVivienda = idVivienda;
            this.nombre = nombre;
            this.apellido = apellido;
            this.cedula = cedula;
            this.telefono = telefono;
        }
    }

    private final JDialog JDPadre;
    private final boolean esEdicion;
    private final RepresentanteData dataInicial;
    private final Runnable onGuardado;

    public FrameFormularioRepresentante(JDialog JDPadre, boolean esEdicion, RepresentanteData dataInicial,
                                         List<ViviendaComboItem> viviendasActivas, Runnable onGuardado) {
        this.JDPadre = JDPadre;
        this.esEdicion = esEdicion;
        this.dataInicial = dataInicial;
        this.onGuardado = onGuardado;

        setLayout(new BorderLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel titulo = new JLabel(esEdicion ? "ACTUALIZAR REPRESENTANTE" : "AGREGAR REPRESENTANTE", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenido.setBorder(new EmptyBorder(18, 22, 14, 22));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);

        JLabel lblVivienda = etiquetaDialogo("Vivienda");
        JComboBox<ViviendaComboItem> comboVivienda = crearComboViviendas();
        comboVivienda.setPreferredSize(new Dimension(280, 30));
        for (ViviendaComboItem item : viviendasActivas) comboVivienda.addItem(item);
        if (dataInicial != null) seleccionarVivienda(comboVivienda, dataInicial.idVivienda);

        JLabel lblNombre = etiquetaDialogo("Nombre");
        JTextField txtNombreLocal = campoDialogo(dataInicial == null ? "" : dataInicial.nombre, "Ej: Carlos");
        txtNombreLocal.setPreferredSize(new Dimension(280, 30));

        JLabel lblApellido = etiquetaDialogo("Apellido");
        JTextField txtApellidoLocal = campoDialogo(dataInicial == null ? "" : dataInicial.apellido, "Ej: Mendoza");
        txtApellidoLocal.setPreferredSize(new Dimension(280, 30));

        // La cédula se guarda completa con su prefijo (ej. "V-12345678"). Si venimos
        // de una edición, separamos el prefijo (V/E) del número para poblar el combo
        // y el campo de texto por separado.
        String nacionalidadInicial = "V";
        String cedulaSoloNumerosInicial = "";
        if (dataInicial != null && dataInicial.cedula != null) {
            String cedulaOriginal = dataInicial.cedula.trim();
            if (!cedulaOriginal.isEmpty() && (Character.toUpperCase(cedulaOriginal.charAt(0)) == 'V'
                    || Character.toUpperCase(cedulaOriginal.charAt(0)) == 'E')) {
                nacionalidadInicial = String.valueOf(Character.toUpperCase(cedulaOriginal.charAt(0)));
                cedulaSoloNumerosInicial = cedulaOriginal.replaceFirst("^[VEve][-\\s]?", "");
            } else {
                cedulaSoloNumerosInicial = cedulaOriginal;
            }
        }

        JLabel lblCedula = etiquetaDialogo("Cédula");
        JComboBox<String> comboNacionalidad = new JComboBox<>(new String[]{"V", "E"});
        comboNacionalidad.setSelectedItem(nacionalidadInicial);
        comboNacionalidad.setFont(ThemeManager.TEXT_NORMAL);
        comboNacionalidad.setBackground(ThemeManager.COLOR_INPUT);
        comboNacionalidad.setForeground(ThemeManager.COLOR_TEXT_DARK);
        comboNacionalidad.setPreferredSize(new Dimension(58, 30));
        comboNacionalidad.setMaximumSize(new Dimension(58, 30));

        JTextField txtCedulaLocal = campoDialogo(cedulaSoloNumerosInicial, "Ej: 12345678");
        txtCedulaLocal.setPreferredSize(new Dimension(200, 30));

        JPanel panelCedula = new JPanel(new BorderLayout(6, 0));
        panelCedula.setOpaque(false);
        panelCedula.add(comboNacionalidad, BorderLayout.WEST);
        panelCedula.add(txtCedulaLocal, BorderLayout.CENTER);

        JLabel lblTelefono = etiquetaDialogo("Teléfono");
        JTextField txtTelefonoLocal = campoDialogo(dataInicial == null ? "" : dataInicial.telefono, "Ej: 04121234567");
        txtTelefonoLocal.setPreferredSize(new Dimension(200, 30));

        restringirSoloLetras(txtNombreLocal, 30);
        restringirSoloLetras(txtApellidoLocal, 30);
        restringirSoloNumeros(txtCedulaLocal, 8);
        restringirSoloNumeros(txtTelefonoLocal, 11);

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
        formPanel.add(panelCedula, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtTelefonoLocal, gbc);

        JButton btnGuardar = ThemeManager.Button(esEdicion ? "Actualizar Representante" : "Agregar Representante");
        btnGuardar.setPreferredSize(new Dimension(260, 36));
        btnGuardar.setMaximumSize(new Dimension(260, 36));

        btnGuardar.addActionListener(e -> guardar(
            (ViviendaComboItem) comboVivienda.getSelectedItem(),
            txtNombreLocal.getText().trim(),
            txtApellidoLocal.getText().trim(),
            String.valueOf(comboNacionalidad.getSelectedItem()),
            txtCedulaLocal.getText().trim(),
            txtTelefonoLocal.getText().trim()
        ));

        contenido.add(formPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(14, 0, 4, 0));
        JPanel wrapBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapBtn.setOpaque(false);
        wrapBtn.add(btnGuardar);
        bottom.add(wrapBtn, BorderLayout.CENTER);
        contenido.add(bottom, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    private void guardar(ViviendaComboItem viviendaSeleccionada, String nombre, String apellido,
                          String nacionalidad, String cedulaNumeros, String telefono) {
        if (viviendaSeleccionada == null) {
            FrameMensaje.error(this, "Debe seleccionar una vivienda.");
            return;
        }

        if (!validarNombre(nombre)) {
            FrameMensaje.error(this, "Nombre inválido.");
            return;
        }

        if (!validarApellido(apellido)) {
            FrameMensaje.error(this, "Apellido inválido.");
            return;
        }

        if (!validarCedula(cedulaNumeros)) {
            FrameMensaje.error(this, "Cédula inválida. Debe contener entre 6 y 8 dígitos.");
            return;
        }

        if (!validarTelefono(telefono)) {
            FrameMensaje.error(this, "Teléfono inválido.");
            return;
        }

        String cedula = nacionalidad + "-" + cedulaNumeros;

        try {
            String miUsuario = Backend.SesionUsuario.getInstancia().getCedula();
            if (miUsuario == null) miUsuario = "Sistema_Java";

            if (esEdicion) {
                ConexionPostgres.comandoDML(
                    "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE representantes SET id_vivienda = ?, nombre = ?, apellido = ?, cedula = ?, telefono = ? WHERE cedula = ?",
                    new Object[]{viviendaSeleccionada.id, nombre, apellido, cedula, telefono, dataInicial.cedula}
                );
            } else {
                ResultSet rsExiste = ConexionPostgres.consultar(
                    "SELECT activo FROM representantes WHERE cedula = ?",
                    new Object[]{cedula}
                );

                if (rsExiste != null && rsExiste.next()) {
                    boolean estaActivo = rsExiste.getBoolean("activo");
                    if (estaActivo) {
                        FrameMensaje.error(this, "Ya existe un representante con esa cédula.");
                        return;
                    }

                    ConexionPostgres.comandoDML(
                        "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "UPDATE representantes SET id_vivienda = ?, nombre = ?, apellido = ?, telefono = ?, activo = true WHERE cedula = ?",
                        new Object[]{viviendaSeleccionada.id, nombre, apellido, telefono, cedula}
                    );
                } else {
                    ConexionPostgres.comandoDML(
                        "DO $$ BEGIN PERFORM set_config('app.usuario_actual', '" + miUsuario + "', true); END $$; "
                                       + "INSERT INTO representantes (id_vivienda, nombre, apellido, cedula, telefono, activo) VALUES (?, ?, ?, ?, ?, true)",
                        new Object[]{viviendaSeleccionada.id, nombre, apellido, cedula, telefono}
                    );
                }
            }

            FrameMensaje.exito(this, "Representante creado/actualizado correctamente.");
            if (onGuardado != null) onGuardado.run();
            JDPadre.dispose();
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo guardar el representante: " + ex.getMessage());
        }
    }

    private JComboBox<ViviendaComboItem> crearComboViviendas() {
        JComboBox<ViviendaComboItem> combo = new JComboBox<>();
        combo.setFont(ThemeManager.TEXT_NORMAL);
        combo.setForeground(ThemeManager.COLOR_TEXT_DARK);
        combo.setBackground(ThemeManager.COLOR_INPUT);
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.COLOR_INPUT, 1));
        return combo;
    }

    private void seleccionarVivienda(JComboBox<ViviendaComboItem> combo, int idVivienda) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ViviendaComboItem item = combo.getItemAt(i);
            if (item.id == idVivienda) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private JLabel etiquetaDialogo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(ThemeManager.COLOR_TEXT);
        label.setFont(ThemeManager.TEXT_SUBTITLE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField campoDialogo(String valorInicial, String placeholder) {
        JTextField field = new JTextField(valorInicial) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && placeholder != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.COLOR_PLACEHOLDER);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        field.setPreferredSize(new Dimension(280, 28));
        field.setBackground(ThemeManager.COLOR_INPUT);
        field.setForeground(ThemeManager.COLOR_TEXT_DARK);
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    /**
     * Valida nombres: solo letras (con tildes/ñ), espacios simples, apóstrofes y
     * guiones para nombres compuestos. No permite números ni espacios dobles,
     * ni que empiece/termine con espacio, apóstrofe o guion.
     */
    private boolean validarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) return false;
        if (nombre.length() < 2 || nombre.length() > 30) return false;
        if (nombre.contains("  ")) return false;
        return nombre.matches("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '\\-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$");
    }

    private boolean validarApellido(String apellido) {
        if (apellido == null || apellido.isEmpty()) return false;
        if (apellido.length() < 2 || apellido.length() > 30) return false;
        if (apellido.contains("  ")) return false;
        return apellido.matches("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '\\-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$");
    }

    /**
     * Valida la parte numérica de la cédula de identidad venezolana (sin el
     * prefijo V/E, que se maneja en un combobox aparte). Legalmente no existe
     * una cantidad de dígitos distinta entre V y E: ambas nacionalidades usan
     * el mismo rango numérico (actualmente hasta ~32 millones, es decir, un
     * máximo de 8 dígitos), por lo que se aplica la misma regla a ambas.
     */
    private boolean validarCedula(String cedula) {
        if (cedula == null || cedula.isEmpty()) return false;
        return cedula.matches("^[1-9][0-9]{4,7}$");
    }

    /**
     * Valida teléfono venezolano: solo dígitos, 11 dígitos en total, comenzando
     * con 0 y seguido de un prefijo válido de celular (0412, 0414, 0416, 0424,
     * 0426) o de un código de área fijo (02XX).
     */
    private boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) return false;
        return telefono.matches("^0(2\\d{2}|4(12|14|16|24|26))\\d{7}$");
    }

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

    private void restringirSoloLetras(JTextField campo, int maxLength) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ '\\-]", "");
                if (filtrado.isEmpty()) return;
                int espacioDisponible = maxLength - fb.getDocument().getLength();
                if (espacioDisponible <= 0) return;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.insertString(fb, offset, filtrado, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String filtrado = text == null ? "" : text.replaceAll("[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ '\\-]", "");
                int largoActual = fb.getDocument().getLength() - length;
                int espacioDisponible = maxLength - largoActual;
                if (espacioDisponible < 0) espacioDisponible = 0;
                if (filtrado.length() > espacioDisponible) filtrado = filtrado.substring(0, espacioDisponible);
                super.replace(fb, offset, length, filtrado, attrs);
            }
        });
    }
}
