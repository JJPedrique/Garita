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

public class FramePagarCuota extends JPanel {

    private final JDialog JDPadre;
    private final int idVivienda;
    private final String numeroVivienda;
    private final String calle;
    private final CuotasService.CuotaPendiente cuota;
    private final Runnable onPagoRealizado;

    public FramePagarCuota(JDialog JDPadre, int idVivienda, String numeroVivienda, String calle,
                            CuotasService.CuotaPendiente cuota, Runnable onPagoRealizado) {
        this.JDPadre = JDPadre;
        this.idVivienda = idVivienda;
        this.numeroVivienda = numeroVivienda;
        this.calle = calle;
        this.cuota = cuota;
        this.onPagoRealizado = onPagoRealizado;

        setLayout(new BorderLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel titulo = new JLabel("PAGAR CUOTA", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        contenido.setBorder(new EmptyBorder(16, 18, 16, 18));

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

        btnPagar.addActionListener(e -> pagar(txtReferencia.getText().trim(), comboTipoPago.getSelectedItem().toString()));

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

        add(encabezado, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    private void pagar(String referencia, String tipoPago) {
        if (!referencia.matches("^\\d{4}$")) {
            FrameMensaje.error(this, "Número de Referencia Inválido.");
            return;
        }

        try {
            ResultSet yaPagada = ConexionPostgres.consultar(
                "SELECT 1 FROM pagos_realizados WHERE id_vivienda = ? AND id_cuota = ? LIMIT 1",
                new Object[]{idVivienda, cuota.id}
            );

            if (yaPagada != null && yaPagada.next()) {
                FrameMensaje.error(this, "La cuota activa ya fue pagada para esta vivienda.");
                return;
            }

            ConexionPostgres.comandoDML(
                "INSERT INTO pagos_realizados (id_vivienda, id_cuota, tipo_pago, referencia, fecha_de_pago) VALUES (?, ?, ?, ?, NOW())",
                new Object[]{idVivienda, cuota.id, tipoPago, referencia}
            );

            try {
                CuotasService.generarReciboPagoPDF(idVivienda, numeroVivienda, calle, cuota, tipoPago, referencia);
            } catch (Exception pdfEx) {
                FrameMensaje.error(this, "Pago registrado, pero no se pudo generar el PDF: " + pdfEx.getMessage());
                if (onPagoRealizado != null) onPagoRealizado.run();
                JDPadre.dispose();
                return;
            }

            FrameMensaje.exito(this, "Pago registrado correctamente.");
            if (onPagoRealizado != null) onPagoRealizado.run();
            JDPadre.dispose();
        } catch (SQLException ex) {
            FrameMensaje.error(this, "No se pudo registrar el pago: " + ex.getMessage());
        }
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
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
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
}
