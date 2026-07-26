package Frontend.Reportes;
import java.io.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.awt.event.*;
import org.openpdf.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Backend.*;
import Frontend.Reportes.DataInputs.*;


public class MenuReporte extends JPanel {

//#region TABLE
    class MyTable extends JPanel {
    ArrayList<String> headers = new ArrayList<>();
    ArrayList<ArrayList<String>> rows = new ArrayList<>();

    JPanel TablePanel = ThemeManager.Panel(new GridBagLayout());

    public MyTable() {
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        TablePanel.setBackground(ThemeManager.COLOR_TEXT_DARK);
        JScrollPane newScroll = new JScrollPane(TablePanel);
        this.add(newScroll, BorderLayout.CENTER);  
        newScroll.setBorder(BorderFactory.createEmptyBorder());
        newScroll.setViewportBorder(null);        
    }

    void UpdateTable(ArrayList<String> newColumns, ArrayList<ArrayList<String>> newRows) {
        headers = newColumns; 
        rows = newRows;
        
        // Limpiamos el panel principal
        TablePanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // 1. DIBUJAR CABECERAS
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.ipady=20;gbc.ipadx=20;
        for (String h : newColumns) {
            JLabel newLabel = ThemeManager.Label(h);
            newLabel.setHorizontalAlignment(SwingConstants.CENTER);
            newLabel.setFont(ThemeManager.TEXT_SUBTITLE);
            // Opcional: poner fondo al label para simular el HeaderPanel original
            newLabel.setOpaque(true);
            newLabel.setBackground(ThemeManager.COLOR_PRIMARY); 
            TablePanel.add(newLabel, gbc);
            gbc.gridx++;
        }

        // 2. DIBUJAR FILAS
        gbc.gridy = 1;
        gbc.ipady=20;gbc.ipadx=20;
        for (ArrayList<String> R : newRows) {
            gbc.gridx = 0;
            
            // Iteramos basándonos en el tamaño de las COLUMNAS, no de la fila.
            // Esto evita que falten celdas si la lista de datos viene incompleta.
            for (int i = 0; i < newColumns.size(); i++) {
                gbc.insets=new Insets(10,0,10,0);
                if(i==0){gbc.insets=new Insets(10,10,10,0);}
                if(i==newColumns.size()-1){gbc.insets=new Insets(10,0,10,10);}
                // Si la fila tiene el dato, lo usamos. Si no, o si está vacío, ponemos un espacio " "
                String cellText = " ";
                if (i < R.size() && R.get(i) != null && !R.get(i).trim().isEmpty()) {
                    cellText = R.get(i);
                }
                
                JLabel cellLabel = ThemeManager.Label(cellText);
                cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cellLabel.setOpaque(true);
                cellLabel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
                
                TablePanel.add(cellLabel, gbc);
                gbc.gridx++;
            }
            gbc.gridy++;
        }

        // Fila extra al final para empujar todo hacia arriba (weighty)
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;gbc.gridx=1;gbc.gridwidth=newColumns.size();
        TablePanel.add(new JLabel(""), gbc);

        this.repaint();
        this.revalidate();
    }
}
//#endregion

//#region SQL QUERY
    public static final Map<String, String> CONSULTAS;

    static {
        Map<String, String> mapaTemporal = new HashMap<>();
        
        mapaTemporal.put("Listado de Viviendas","SELECT " +
                        " V.calle AS \"Calle\"," +
                        " V.numero_vivienda AS \"Número de Vivienda\", " +
                        " CASE " +
                        " WHEN R.id IS NULL THEN 'No asignado' " +
                        " ELSE TRIM(CONCAT(R.nombre, ' ', R.apellido)) " +
                        " END AS \"Propietario\"," +
                        " COALESCE(CAST(R.cedula AS VARCHAR), 'No asignada') AS \"Cédula\", " +
                        " COALESCE(R.telefono, 'No asignado') AS \"Teléfono\"" +
                        " FROM viviendas AS V" +
                        " LEFT JOIN representantes AS R ON V.id = R.id_vivienda" +
                        " WHERE V.activo = True");

        mapaTemporal.put("Listado de Carnets","SELECT C.codigo AS \"Código\"," +
                        " V.numero_vivienda AS \"Número de Vivienda\"," +
                        " V.calle AS \"Calle\"" +
                        " FROM viviendas AS V" +
                        " LEFT JOIN carnets AS C ON  V.id = C.id_vivienda" +
                        " WHERE V.activo = True AND C.activo = True");

        mapaTemporal.put("Lista de accesos por Carnets", "SELECT A.fecha_hora AS \"Fecha-Hora\", " +
                        " A.tipo AS \"Tipo\", " +
                        " A.estado AS \"Estado\", " +
                        " C.codigo AS \"Carnet\", " +
                        " V.calle AS \"Calle\", " +
                        " V.numero_vivienda AS \"Número Vivienda\"" +
                        " FROM accesos AS A" +
                        " JOIN carnets AS C ON C.id = A.id_carnet" +
                        " JOIN viviendas AS V ON V.id = C.id_vivienda" +
                        " WHERE A.nombre_visita IS NULL");
                        
        mapaTemporal.put("Lista de accesos por Visita","SELECT A.fecha_hora AS \"Fecha-Hora\", " +
                        " A.tipo AS \"Tipo\", " +
                        " A.estado AS \"Estado\", " +
                        " A.nombre_visita AS \"Nombre de Visita\"" +
                        " FROM accesos AS A" +
                        " WHERE A.id_carnet IS NULL");

        mapaTemporal.put("Listado de Cuotas","SELECT C.descripcion AS \"Descripción\"," +
                        " C.monto AS \"Monto\"," +
                        " C.fecha_emision AS \"Fecha Emisión\"," +
                        " C.fecha_limite AS \"Fecha Limite\"," +
                        " COUNT(PR.id) AS \"Pagaron\"," +
                        " C.monto*COUNT(PR.id) AS \"Acumulado\"" +
                        " FROM cuotas AS C" +
                        " LEFT JOIN pagos_realizados AS PR ON C.id = PR.id_cuota" +
                        " WHERE C.borrada = False AND C.activo = True" +
                        " GROUP BY C.id");

        mapaTemporal.put("Listado de Morosidad", "SELECT V.calle \"Calle\"," +
                        " V.numero_vivienda \"Número de Vivienda\"," +
                        " solvencia(V.id) AS \"Estado\"," +
                        " deuda(V.id) AS \"Deuda\"" +
                        " FROM viviendas AS  V" +
                        " WHERE V.activo = True");
        
        mapaTemporal.put("Historial de Pagos", "SELECT V.calle AS \"Calle\"," +
                        " V.numero_vivienda AS \"Número de Vivienda\"," +
                        " C.descripcion AS \"Descripción\"," +
                        " C.monto AS \"Monto\"," +
                        " PR.tipo_pago AS \"Tipo de Pago\", " +
                        " PR.referencia AS \"Referencia\"" +
                        " FROM pagos_realizados AS PR" +
                        " JOIN viviendas AS V ON V.id = PR.id_vivienda" +
                        " JOIN cuotas AS C ON V.id = PR.id_cuota");

        mapaTemporal.put("Listado de Usuarios", "SELECT U.rol AS \"Rol\", " +
                        " CONCAT(U.nombre,' ',U.apellido) AS \"Nombre Completo\"," +
                        " U.cedula AS \"Cédula\", " +
                        " U.telefono AS \"Teléfono\"" +
                        " FROM usuarios AS U" +
                        " WHERE activo = true");

        mapaTemporal.put("Historial de Bitacora","SELECT B.usuario AS \"Usuario\", " +
                        " B.accion AS \"Acción\", " +
                        " B.tabla_modificada AS \"Módulo\", " +
                        " B.fecha_modificacion AS \"Fecha\"" +
                        " FROM bitacoras AS B");


        CONSULTAS = Collections.unmodifiableMap(mapaTemporal);
    }

//#endregion
    
//#region FRONTEND

    //Modulo QUERY SELECTOR
    Map<String,String> TableHeader = new HashMap<>();
    JComboBox<String> Modulos;

    //Visible SELECT
    JPanel ColumnFilterPanel = ThemeManager.Panel(new GridBagLayout()); 
    ArrayList<JCheckBox> ColumnFilter = new ArrayList<>();  

    //Condition WHERE
    JComboBox<String> RowFilterSelector = new JComboBox<>();
    JPanel RowFilterPanel = ThemeManager.Panel(new GridBagLayout());
    ArrayList<Input> RowFilter = new ArrayList<>();    
    
    //Sort ORDER BY
    JComboBox<String> OrderColumn,OrderBy;

    //Limit LIMIT
    JSpinner LimitFrom,LimitTo;
    
    //Table
    MyTable Table = new MyTable();

    public MenuReporte() throws SQLException{
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        JTabbedPane Menu = new JTabbedPane();
        Menu.addTab("Modulo",Modulo());
        Menu.addTab( "Filtros",Filtros());
        Menu.addTab("Imprimir",Imprimir());
        UIManager.put("TabbedPane.tabAreaBackground", ThemeManager.COLOR_BACKGROUND);
        UIManager.put("TabbedPane.background", ThemeManager.COLOR_BACKGROUND);
        Menu.setForeground(ThemeManager.COLOR_TEXT);
        Menu.setBackground(ThemeManager.COLOR_BACKGROUND);
        Menu.setFont(ThemeManager.TEXT_NORMAL);

        this.add(Menu,BorderLayout.WEST);   
        this.add(Table, BorderLayout.CENTER);           

        //UPDATES
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {SearchSQL();}
        });

        ChangeModule("Listado de Viviendas");
        SearchSQL();
    }

    JPanel Modulo(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(10,10,10,10);
        
        gbc.gridwidth=2;
        JLabel Text = ThemeManager.Label("FILTRAR COLUMNAS");
        Text.setFont(ThemeManager.TEXT_SUBTITLE);
        Text.setHorizontalAlignment(SwingConstants.CENTER);
        newPanel.add(Text,gbc);

        gbc.gridy=1;gbc.gridwidth=1;
        JLabel modulos = ThemeManager.Label("Modulo");
        
        newPanel.add(modulos,gbc);

        gbc.gridx=1;gbc.ipady=10;
        Modulos = ThemeManager.StringComboBox();
        for(String k : CONSULTAS.keySet()){Modulos.addItem(k);}  
        Modulos.setSelectedItem("Listado de Viviendas");
        newPanel.add(Modulos,gbc);
        Modulos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ChangeModule(Modulos.getSelectedItem().toString());
                    SearchSQL();
                } catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridx=0; gbc.gridy=2;gbc.ipady=0;
        gbc.gridwidth=2; gbc.weightx=1;gbc.weighty=1;

        ColumnFilterPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        JScrollPane newScroll = new JScrollPane(ColumnFilterPanel);
        newPanel.add(newScroll,gbc);
        newScroll.setBorder(BorderFactory.createEmptyBorder());
        newScroll.setViewportBorder(null);   

        return newPanel;
    }

    JPanel Filtros(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,10,10,10);
        gbc.weightx=1;
         
        JLabel Text = ThemeManager.Label("FILTRAR FILAS");
        Text.setFont(ThemeManager.TEXT_SUBTITLE);
        Text.setHorizontalAlignment(SwingConstants.CENTER);
        newPanel.add(Text,gbc);
        
        gbc.fill=GridBagConstraints.BOTH;
        gbc.gridx=0; gbc.gridy=1;gbc.ipady=0;
        gbc.weightx=1;gbc.weighty=1;

        RowFilterPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        JScrollPane newScroll = new JScrollPane(RowFilterPanel);
        newPanel.add(newScroll,gbc);
        newScroll.setBorder(BorderFactory.createEmptyBorder());
        newScroll.setViewportBorder(null);

        return newPanel;
    }

    JPanel Imprimir(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0; gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,10,10,10);    
        gbc.ipady=10;gbc.gridwidth=2;


        JLabel Text = ThemeManager.Label("OTROS");
        Text.setFont(ThemeManager.TEXT_SUBTITLE);
        Text.setHorizontalAlignment(SwingConstants.CENTER);
        newPanel.add(Text,gbc);

        gbc.gridy=1;gbc.gridwidth=1;
        JLabel ordenar = ThemeManager.Label("Ordenar de manera");
        newPanel.add(ordenar,gbc);   

        gbc.gridy=2; gbc.gridx=0; 
        OrderColumn = ThemeManager.StringComboBox();
        OrderColumn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OrderColumn.getItemCount()==0){return;}
                SearchSQL();}
        });
        newPanel.add(OrderColumn,gbc);

        gbc.gridx=1;     
        String OrderByValues[] = {"Ascendente","Descendente"};
        OrderBy = ThemeManager.StringComboBox();
        for(String k : OrderByValues){OrderBy.addItem(k);}
        OrderBy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {SearchSQL();}
        });          
        newPanel.add(OrderBy,gbc);

        gbc.gridy=3; gbc.gridx=0;
        JLabel empezar = ThemeManager.Label("Empezar desde fila");
        newPanel.add(empezar,gbc);    
        
        gbc.gridx=1; 
        LimitFrom = new JSpinner( new SpinnerNumberModel(0, 0, 1000, 1));
        newPanel.add(LimitFrom,gbc);

        gbc.gridy=4; gbc.gridx=0;
        JLabel limite = ThemeManager.Label("Maximo de filas");
        newPanel.add(limite,gbc);   

        gbc.gridx=1; 
        LimitTo = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        newPanel.add(LimitTo,gbc);

        gbc.gridy=5;gbc.gridx=0; gbc.gridwidth=3;gbc.ipady=0;
        JButton BtnImprimir =  ThemeManager.Button("Exportar a PDF");
        newPanel.add(BtnImprimir,gbc);
        BtnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImprimirPDF();
            }
        });
 
        gbc.gridy=6; gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        newPanel.add(new JLabel(),gbc);

        return newPanel;
    }

//#endregion

//#region BACKEND
    void ChangeModule(String Module) throws SQLException{
        for (Component C : ColumnFilterPanel.getComponents()) {ColumnFilterPanel.remove(C);}
        for (Component C : RowFilterPanel.getComponents()) {RowFilterPanel.remove(C);}
        TableHeader.clear();
        ColumnFilter.clear();
        RowFilter.clear();
        RowFilterSelector.removeAllItems();
        OrderColumn.removeAllItems();

        String SQL_query = CONSULTAS.get(Module);
        String TempView = "CREATE OR REPLACE TEMP VIEW vista_analisis_columnas AS " + SQL_query;
        ConexionPostgres.comandoDML(TempView, new Object[]{});

        String SQL_HEADER = "SELECT column_name AS columna_nombre, data_type AS tipo_base " + 
                            "FROM information_schema.columns " + 
                            "WHERE table_name = 'vista_analisis_columnas' " + 
                            "ORDER BY ordinal_position;";

        ResultSet RS_DATA = ConexionPostgres.consultar(SQL_HEADER, null);
        
        TempView = "DROP VIEW vista_analisis_columnas;";
        ConexionPostgres.comandoDML(TempView, new Object[]{});

        while (RS_DATA.next()) {            
            TableHeader.put(RS_DATA.getString("columna_nombre"), RS_DATA.getString("tipo_base"));
        }

        //View
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;
        gbc.gridy = 0; 
        gbc.insets = new Insets(10,10,10,10);


        for(String k : TableHeader.keySet()){
            JCheckBox newCheck = ThemeManager.CheckBox(k);
            newCheck.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {SearchSQL();}
            });

            ColumnFilterPanel.add(newCheck,gbc);
            ColumnFilter.add(newCheck);

            Input tempCondition = null;
            switch (TableHeader.get(k)) {
                case "character varying": tempCondition = new StringInput(k); break;
                case "bigint": tempCondition = new IntegerInput(k); break;
                case "numeric": tempCondition = new DecimalInput(k); break;
                case "timestamp without time zone": tempCondition = new DateInput(k); break;
                default: tempCondition = new StringInput(k); break;
            }
             RowFilterPanel.add(tempCondition,gbc);
            RowFilter.add(tempCondition);
            
            gbc.gridy++;

            RowFilterSelector.addItem(k);
            OrderColumn.addItem(k);            
        }

        gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        ColumnFilterPanel.add(ThemeManager.Label(""),gbc);
        RowFilterPanel.add(ThemeManager.Label(""),gbc);

        this.revalidate();
        this.repaint();
    }

    void SearchSQL(){
        //Frontend
        String Modulo = CONSULTAS.get(Modulos.getSelectedItem().toString());

        ArrayList<String> SelectedColumn = new ArrayList<>();
        for(JCheckBox C : ColumnFilter) {
            if(!C.isSelected()){continue;}
            SelectedColumn.add("\""+C.getText()+"\"");
        }

        ArrayList<Object> Param = new ArrayList<>();
        ArrayList<String> SelectedRowFilter = new ArrayList<>();
        for(Input R : RowFilter) {
            String value = R.GetValue();
            if(value==""){continue;}
            if(value=="???"){return;}
            
            if(R.getClass().getName().contains("StringInput")){Param.add(value);}
            if(R.getClass().getName().contains("DecimalInput")){Param.add(Double.parseDouble(value));}
            if(R.getClass().getName().contains("IntegerInput")){Param.add(Integer.parseInt(value));}
            if(R.getClass().getName().contains("BooleanInput")){Param.add(Boolean.parseBoolean(value));}
            if(R.getClass().getName().contains("DateInput")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if(R.GetValue().contains("\n")){
                    System.out.println(R.GetValue());
                    Param.add(LocalDateTime.parse(value.split("\n")[0], formatter));
                    Param.add(LocalDateTime.parse(value.split("\n")[1], formatter));
                }
                else{Param.add(LocalDateTime.parse(value, formatter));}
            }            
            SelectedRowFilter.add(R.GetCondition());
        }

        //Backend
        if (SelectedColumn.size()==0) {
            ThemeManager.MostrarMensajeError(this, "DEBE MOSTRAR AL MENOS UNA COLUMNA");
            for (JCheckBox CB : ColumnFilter) {CB.setSelected(true);}
            SearchSQL();
            return;
        }
        
        String SELECT = "SELECT " + String.join(", ", SelectedColumn);
        String FROM = "FROM ( " + Modulo + " ) as IDK";  
        String WHERE = !Param.isEmpty() ? "WHERE " + String.join(" AND ", SelectedRowFilter) : "";
        String ORDER_BY = "ORDER BY \""+ OrderColumn.getSelectedItem().toString() + "\" " + (OrderBy.getSelectedItem().toString()=="Ascendente" ? "ASC" : "DESC");
        String LIMIT = "LIMIT "+ LimitTo.getValue().toString();
        String OFFSET = "OFFSET "+ LimitFrom.getValue().toString();

        String MAIN_QUERY = String.join(" ",new String[]{SELECT,FROM,WHERE,ORDER_BY,LIMIT,OFFSET});
        
        //BDD
        try {
            ResultSet RS_DATA = ConexionPostgres.consultar(MAIN_QUERY, Param.toArray());
            ArrayList<ArrayList<String>> Rows = new ArrayList<>();

            while (RS_DATA.next()) {            
                ArrayList<String> newData = new ArrayList<>();
                for(String h: SelectedColumn){newData.add(RS_DATA.getString(h.replace("\"", "")));}
                Rows.add(newData);
            }

            for(int i = 0; i <SelectedColumn.size();i++){SelectedColumn.set(i,SelectedColumn.get(i).replace("\"",""));}
            Table.UpdateTable(SelectedColumn,Rows);
        } catch (Exception e) {System.out.println("Something went Wrong");}
        // System.out.println("SQL: "+MAIN_QUERY);
        this.repaint();
        this.revalidate();
    }
//#endregion

//#region EXPORTAR PDF
    public void ImprimirPDF() {
        // 1. Crear el objeto Documento
        Document documento = new Document();        
        try {
            String rutaDescargas = System.getProperty("user.home") + 
                                java.io.File.separator + "Downloads" + java.io.File.separator + 
                                "Reporte Garita - "+ Modulos.getSelectedItem().toString()+".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(rutaDescargas));
            documento.open();

            Paragraph encabezado = new Paragraph(
                "REPUBLICA BOLIVARIANA DE VENEZUELA\n" +
                "MUNICIPIO MARACAIBO - PARROQUIA RAUL LEONI\n" +
                "ASOCIACION DE PROPIETARIOS Y VECINOS DE LA \"URB. SANTA FE III ETAPA\"\n" +
                "Rif: J29613737-4"
            );
            encabezado.setSpacingAfter(12);
            documento.add(encabezado);

            Paragraph titulo = new Paragraph("Reporte Garita - Listado de "+ Modulos.getSelectedItem().toString());
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            float[] anchosColumnas = new float[Table.headers.size()];
            for (int i = 0; i < Table.headers.size(); i++) {anchosColumnas[i] = 1f;}
            PdfPTable tabla = new PdfPTable(anchosColumnas);
            tabla.setWidthPercentage(100); // Que ocupe el 100% del ancho de la página

            String[] encabezados = new String[Table.headers.size()];
            for (int i = 0; i < Table.headers.size(); i++) {encabezados[i] = Table.headers.get(i);}
            
        
            String[][] datos = new String[Table.rows.size()][Table.headers.size()];  
            for (int i = 0; i < Table.rows.size(); i++) {
                for (int j = 0; j < Table.headers.size(); j++) {
                    Object valor = Table.rows.get(i).get(j);
                    datos[i][j] = (valor != null) ? valor.toString() : "";
                }
            }           

            for (String textoHeader : encabezados) {
                PdfPCell celdaHeader = new PdfPCell(new Phrase(textoHeader));
                celdaHeader.setBackgroundColor(ThemeManager.COLOR_SECONDARY); // Color de fondo azul
                celdaHeader.setHorizontalAlignment(0);
                celdaHeader.setPadding(8); // Espaciado interno de la celda
                tabla.addCell(celdaHeader);
            }

            for (String[] fila : datos) {
                for(String columns : fila){
                    PdfPCell newCelda = new PdfPCell(new Phrase(columns));
                    //newCelda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    newCelda.setPadding(6);
                    tabla.addCell(newCelda);
                }
            }
               
            documento.add(tabla);

            Paragraph piePagina = new Paragraph("Av. 84 URB. SANTA FE III ETAPA, PARROQUIA RAÚL LEONI, MUNICIPIO MARACAIBO - EDO. ZULIA Teléfono: 0412-7512230 / 0412-0794503");
            piePagina.setSpacingBefore(16);
            documento.add(piePagina);

            ThemeManager.MostrarMensajeExito(this, "Se genero el reporte en la carpeta descarga");

        } catch (DocumentException | FileNotFoundException e) {
            ThemeManager.MostrarMensajeError(this, "ERROR - No se genero el reporte");
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }
//#endregion
}