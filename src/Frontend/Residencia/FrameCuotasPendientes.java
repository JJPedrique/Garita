package Frontend.Residencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Backend.ThemeManager;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FrameCuotasPendientes extends JPanel {

    public FrameCuotasPendientes(JDialog JDPadre, int idVivienda, String numeroVivienda, String calle) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        ArrayList<CuotasService.CuotaPendiente> pendientes;
        CuotasService.DatosConstancia datos;
        try {
            pendientes = CuotasService.obtenerCuotasPendientesVivienda(idVivienda);
            datos = CuotasService.obtenerDatosConstancia(idVivienda);
        } catch (SQLException ex) {
            pendientes = new ArrayList<>();
            datos = new CuotasService.DatosConstancia("SIN REPRESENTANTE REGISTRADO", "NO REGISTRADA");
        }

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(ThemeManager.COLOR_PRIMARY);
        encabezado.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel titulo = new JLabel("CUOTAS PENDIENTES", SwingConstants.CENTER);
        titulo.setFont(ThemeManager.TEXT_SUBTITLE);
        titulo.setForeground(ThemeManager.COLOR_TEXT);
        encabezado.add(titulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        contenido.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 10, 6);

        JPanel panelDatos = new JPanel(new GridLayout(2, 2, 8, 8));
        panelDatos.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        panelDatos.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelDatos.add(crearEtiquetaInfo("Vivienda", numeroVivienda + " - " + calle));
        panelDatos.add(crearEtiquetaInfo("Representante", datos.nombreCompleto));
        panelDatos.add(crearEtiquetaInfo("Cédula", datos.cedula));
        panelDatos.add(crearEtiquetaInfo("Estado", CuotasService.tieneCuotaVencida(pendientes) ? "Moroso" : "Solvente"));

        String[] columnas = {"Cuota", "Monto", "Fecha Emisión", "Fecha Límite"};
        DefaultTableModel modeloPendientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat fechaFormato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (CuotasService.CuotaPendiente cuota : pendientes) {
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

        add(encabezado, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    private JLabel crearEtiquetaInfo(String titulo, String valor) {
        JLabel label = new JLabel("<html><b>" + titulo + ":</b> " + valor + "</html>");
        label.setForeground(ThemeManager.COLOR_TEXT);
        label.setFont(ThemeManager.TEXT_NORMAL);
        return label;
    }
}
