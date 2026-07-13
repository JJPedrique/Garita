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

public class FrameFormularioVivienda extends JPanel {

    private final JDialog JDPadre;
    private final boolean esEdicion;
    private final String numeroOriginal;
    private final Runnable onGuardado;

    public FrameFormularioVivienda(JDialog JDPadre, boolean esEdicion, String numeroOriginal,
                                    String calleInicial, Runnable onGuardado) {
        this.JDPadre = JDPadre;
        this.esEdicion = esEdicion;
        this.numeroOriginal = numeroOriginal;
        this.onGuardado = onGuardado;

        setLayout(new BorderLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel titulo = new JLabel(esEdicion ? "ACTUALIZAR VIVIENDA" : "AGREGAR VIVIENDA", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND);
        contenido.setBorder(new EmptyBorder(20, 24, 16, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblCalle = etiquetaDialogo("Calle");
        JTextField txtCalleLocal = campoDialogo(calleInicial == null ? "" : calleInicial, "Ej: Calle Los Jabillos");
        txtCalleLocal.setPreferredSize(new Dimension(260, 32));

        JLabel lblNumero = etiquetaDialogo("Número de Casa");
        JTextField txtNumeroLocal = campoDialogo(numeroOriginal == null ? "" : numeroOriginal, "Ej: A-11");
        txtNumeroLocal.setPreferredSize(new Dimension(260, 32));

        restringirCalle(txtCalleLocal, 30);
        restringirNumeroVivienda(txtNumeroLocal, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formPanel.add(lblCalle, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtCalleLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.insets = new Insets(16, 8, 8, 8);
        formPanel.add(lblNumero, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtNumeroLocal, gbc);

        JButton btnGuardar = ThemeManager.Button(esEdicion ? "Actualizar Vivienda" : "Agregar Vivienda");
        btnGuardar.setPreferredSize(new Dimension(230, 38));
        btnGuardar.setMaximumSize(new Dimension(230, 38));

        btnGuardar.addActionListener(e -> guardar(txtCalleLocal.getText().trim(), txtNumeroLocal.getText().trim()));

        contenido.add(formPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(16, 0, 4, 0));
        JPanel wrapBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapBtn.setOpaque(false);
        wrapBtn.add(btnGuardar);
        bottom.add(wrapBtn, BorderLayout.CENTER);
        contenido.add(bottom, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    private void guardar(String calle, String numero) {
        if (!validarCalle(calle)) {
            FrameMensaje.error(this, "Calle y Avenida inválida.");
            return;
        }

        if (!validarNumero(numero)) {
            FrameMensaje.error(this, "Número de Casa inválido.");
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
                        FrameMensaje.error(this, "Ya existe una vivienda con ese número.");
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

            FrameMensaje.exito(this, "Vivienda creada/actualizada correctamente.");
            if (onGuardado != null) onGuardado.run();
            JDPadre.dispose();
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo guardar la vivienda: " + ex.getMessage());
        }
    }

    private JLabel etiquetaDialogo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(ThemeManager.COLOR_TEXT);
        label.setFont(ThemeManager.TEXT_SUBTITLE);
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
        field.setBackground(ThemeManager.COLOR_INPUT);
        field.setForeground(ThemeManager.COLOR_TEXT_DARK);
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
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
}
