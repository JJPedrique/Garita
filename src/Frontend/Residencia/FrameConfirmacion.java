package Frontend.Residencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.ThemeManager;

import java.awt.*;

public class FrameConfirmacion extends JPanel {

    public FrameConfirmacion(JDialog JDPadre, String titulo, String mensaje, Color acento,
                              String textoAceptar, String textoCancelar, boolean[] resultado) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(acento);
        encabezado.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setFont(ThemeManager.TEXT_SUBTITLE);
        encabezado.add(lblTitulo, BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout(10, 0));
        cuerpo.setBackground(ThemeManager.COLOR_BACKGROUND);
        cuerpo.setBorder(new EmptyBorder(18, 18, 16, 18));

        JLabel icono = new JLabel("!", SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(34, 34));
        icono.setOpaque(true);
        icono.setBackground(acento);
        icono.setForeground(ThemeManager.COLOR_TEXT);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel mensajeLabel = new JLabel("<html><div style='width:280px;'>" + mensaje + "</div></html>", SwingConstants.LEFT);
        mensajeLabel.setForeground(ThemeManager.COLOR_TEXT);
        mensajeLabel.setFont(ThemeManager.TEXT_NORMAL);

        cuerpo.add(icono, BorderLayout.WEST);
        cuerpo.add(mensajeLabel, BorderLayout.CENTER);

        JButton cancelar = new JButton(textoCancelar);
        cancelar.setFont(ThemeManager.TEXT_SMALL);
        cancelar.setForeground(ThemeManager.COLOR_TEXT);
        cancelar.setBackground(new Color(65, 65, 65));
        cancelar.setFocusPainted(false);
        cancelar.setBorder(new EmptyBorder(6, 14, 6, 14));
        cancelar.addActionListener(e -> JDPadre.dispose());

        JButton aceptar = ThemeManager.Button(textoAceptar);
        aceptar.setBackground(acento);
        aceptar.setMaximumSize(new Dimension(120, 32));
        aceptar.setPreferredSize(new Dimension(120, 32));
        aceptar.addActionListener(e -> {
            resultado[0] = true;
            JDPadre.dispose();
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pie.setOpaque(false);
        pie.add(cancelar);
        pie.add(aceptar);

        cuerpo.add(pie, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(cuerpo, BorderLayout.CENTER);
    }

    public static boolean confirmar(Component padre, String titulo, String mensaje, Color acento,
                                     String textoAceptar, String textoCancelar) {
        Window owner = SwingUtilities.getWindowAncestor(padre);
        final JDialog dialogo;
        if (owner instanceof Frame) {
            dialogo = new JDialog((Frame) owner, titulo, true);
        } else if (owner instanceof Dialog) {
            dialogo = new JDialog((Dialog) owner, titulo, true);
        } else {
            dialogo = new JDialog();
            dialogo.setTitle(titulo);
            dialogo.setModal(true);
        }

        boolean[] resultado = {false};

        dialogo.setSize(440, 190);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FrameConfirmacion(dialogo, titulo, mensaje, acento, textoAceptar, textoCancelar, resultado));
        dialogo.setVisible(true);

        return resultado[0];
    }
}
