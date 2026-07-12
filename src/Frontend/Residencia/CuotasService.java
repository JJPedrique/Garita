package Frontend.Residencia;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

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
import java.util.List;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

public class CuotasService {

    public static class CuotaPendiente {
        public final int id;
        public final String descripcion;
        public final BigDecimal monto;
        public final Timestamp fechaEmision;
        public final Timestamp fechaLimite;

        public CuotaPendiente(int id, String descripcion, BigDecimal monto, Timestamp fechaEmision, Timestamp fechaLimite) {
            this.id = id;
            this.descripcion = descripcion;
            this.monto = monto;
            this.fechaEmision = fechaEmision;
            this.fechaLimite = fechaLimite;
        }
    }

    public static class DatosConstancia {
        public final String nombreCompleto;
        public final String cedula;

        public DatosConstancia(String nombreCompleto, String cedula) {
            this.nombreCompleto = nombreCompleto;
            this.cedula = cedula;
        }
    }

    public static CuotaPendiente obtenerCuotaActivaPendiente(int idVivienda) throws SQLException {
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
        return null;
    }

    public static ArrayList<CuotaPendiente> obtenerCuotasPendientesVivienda(int idVivienda) throws SQLException {
        ArrayList<CuotaPendiente> pendientes = new ArrayList<>();
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
        return pendientes;
    }

    public static DatosConstancia obtenerDatosConstancia(int idVivienda) throws SQLException {
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

    /**
     * Una vivienda es "Moroso" solo si tiene al menos una cuota pendiente cuya
     * fecha límite ya pasó. Una cuota pendiente que todavía está dentro de su
     * plazo (fecha límite futura) no cuenta como morosidad.
     */
    public static boolean tieneCuotaVencida(List<CuotaPendiente> pendientes) {
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
    public static CuotaPendiente obtenerCuotaVencidaMasAntigua(List<CuotaPendiente> pendientes) {
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

    /**
     * Genera el PDF de constancia/recibo de pago y lo abre con la aplicación
     * predeterminada del sistema. Lanza SQLException/DocumentException/
     * FileNotFoundException para que el llamador decida cómo notificar el error.
     */
    public static void generarReciboPagoPDF(int idVivienda, String numeroVivienda, String calle,
                                             CuotaPendiente cuota, String tipoPago, String referencia)
            throws SQLException, DocumentException, FileNotFoundException {
        Document documento = new Document();
        try {
            DatosConstancia datos = obtenerDatosConstancia(idVivienda);
            String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

            ArrayList<CuotaPendiente> pendientesActuales = obtenerCuotasPendientesVivienda(idVivienda);
            boolean esMoroso = tieneCuotaVencida(pendientesActuales);
            String estadoVivienda = esMoroso ? "Moroso" : "Solvente";

            // Si está moroso, el mes/año de referencia es el de la cuota vencida
            // más antigua (la que realmente origina la morosidad), no el de la cuota
            // que se acaba de pagar en esta transacción. Si está solvente, se usa la
            // cuota recién pagada como referencia de "al día hasta".
            CuotaPendiente cuotaVencidaMasAntigua = esMoroso ? obtenerCuotaVencidaMasAntigua(pendientesActuales) : null;
            java.sql.Timestamp fechaReferencia = cuotaVencidaMasAntigua != null ? cuotaVencidaMasAntigua.fechaLimite : cuota.fechaLimite;

            String anioCuota = new SimpleDateFormat("yyyy").format(fechaReferencia);
            String mesCuota = new SimpleDateFormat("MMMM", new Locale("es", "VE")).format(fechaReferencia).toUpperCase();

            String nombreArchivo = "Constancia_Solvencia_" + numeroVivienda + "_" +
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
                "propietario en la calle " + calle + " Casa N° " + numeroVivienda + " se encuentra " + estadoVivienda + " " +
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
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    private static void agregarCeldaInfo(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta));
        celdaEtiqueta.setBackgroundColor(ThemeManager.COLOR_SECONDARY);
        celdaEtiqueta.setPadding(6);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor));
        celdaValor.setPadding(6);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}
