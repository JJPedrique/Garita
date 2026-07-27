package Frontend.Residencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Backend.ThemeManager;

import java.awt.*;

public class FrameMensaje extends JPanel {

    public FrameMensaje(JDialog JDPadre, String titulo, String mensaje, Color acento, String textoBoton, String iconoChar) {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints c = new GridBagConstraints();

        JLabel icono = new JLabel(iconoChar, SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(36, 36));
        icono.setOpaque(true);
        icono.setBackground(acento);
        icono.setForeground(ThemeManager.COLOR_TEXT);
        icono.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel texto = new JLabel("<html><div style='width:280px;'>" + mensaje + "</div></html>", SwingConstants.LEFT);
        texto.setForeground(ThemeManager.COLOR_TEXT);
        texto.setFont(ThemeManager.TEXT_NORMAL);

        JButton aceptar = ThemeManager.Button(textoBoton);
        aceptar.setBackground(acento);
        aceptar.setPreferredSize(new Dimension(120, 32));
        aceptar.setMaximumSize(new Dimension(120, 32));
        aceptar.addActionListener(e -> JDPadre.dispose());
        aceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aceptar.setBackground(acento.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aceptar.setBackground(acento);
            }
        });

        c.insets = new Insets(10, 10, 5, 10);
        c.weightx = 0.0;
        c.gridx = 0; c.gridy = 0; add(icono, c);
        c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL; add(texto, c);
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 5, 5, 5); add(aceptar, c);
    }

    //region Atajos estáticos
    private static void mostrar(Component padre, String titulo, String mensaje, Color acento, String textoBoton, String icono) {
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

        dialogo.setSize(520, 220);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(new FrameMensaje(dialogo, titulo, mensaje, acento, textoBoton, icono));
        dialogo.setVisible(true);
    }

    public static void error(Component padre, String mensaje) {
        mostrar(padre, "Sistema Garita - ERROR", mensaje, ThemeManager.COLOR_ERROR, "Aceptar", "X");
    }

    
    public static void exito(Component padre, String mensaje) {
        mostrar(padre, "Sistema Garita", mensaje, ThemeManager.COLOR_PRIMARY, "Aceptar", "i");
    }

    
    public static void exitoEliminacion(Component padre, String mensaje) {
        mostrar(padre, "Sistema Garita - Eliminación Exitosa", mensaje, ThemeManager.COLOR_PRIMARY, "Aceptar", "✓");
    }
    //endregion
}
