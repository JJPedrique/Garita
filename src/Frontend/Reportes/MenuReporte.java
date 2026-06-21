package Frontend.Reportes;
import java.io.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import org.openpdf.text.*;
import org.openpdf.text.pdf.*;
import javax.swing.table.DefaultTableModel;

import Backend.*;
import Frontend.Reportes.DataInputs.*;


public class MenuReporte extends JPanel {
    ConexionPostgres DB = new ConexionPostgres();

    //#region SQL QUERY
    public static final Map<String, String> CONSULTAS;

    static {
        Map<String, String> mapaTemporal = new HashMap<>();
        
        mapaTemporal.put("Vivienda", "SELECT viviendas.calle AS Calle, " +
                "viviendas.numero_vivienda AS Vivienda, " +
                "COUNT(representantes.id) AS Representantes, " +
                "COUNT(carnets.id) AS Carnets " +
                "FROM viviendas " +
                "JOIN representantes ON viviendas.id = representantes.id_vivienda " +
                "JOIN carnets ON viviendas.id = carnets.id_vivienda " +
                "GROUP BY viviendas.calle, viviendas.numero_vivienda");

        mapaTemporal.put("Vecinos", "SELECT viviendas.calle AS Calle, " +
                "viviendas.numero_vivienda AS Vivienda, " +
                "representantes.nombre AS Nombre, " +
                "representantes.apellido AS Apellido, " +
                "representantes.cedula AS Cedula, " +
                "representantes.telefono AS Telefono " +
                "FROM representantes " +
                "JOIN viviendas ON viviendas.id = representantes.id_vivienda ");

        mapaTemporal.put("Carnets", "SELECT viviendas.calle AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " +
                "carnets.codigo AS Codigo " + 
                "FROM carnets " +
                "JOIN viviendas ON viviendas.id = carnets.id_vivienda");

        mapaTemporal.put("RegistrosAcceso", "SELECT accesos.fecha_hora AS \"Fecha de Acceso\"," + 
                " accesos.tipo AS Tipo, " + 
                " accesos.estado AS Estado, " + 
                " viviendas.calle AS Calle, " + 
                " viviendas.numero_vivienda AS Vivienda, " + 
                " carnets.codigo AS Codigo, " + 
                " accesos.nombre_visita AS Visita " + 
                "FROM accesos " + 
                "LEFT JOIN carnets ON carnets.id = accesos.id_carnet " + 
                "LEFT JOIN viviendas ON viviendas.id = carnets.id_vivienda");

        mapaTemporal.put("Cuotas", "SELECT cuotas.descripcion AS Descripcion, " + 
                "cuotas.fecha_emision AS \"Fecha de Emision\", " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.fecha_limite AS \"Fecha Limite de pago\", " + 
                "COUNT(pagos_realizados.id) AS \"Cuotas Pagadas\", " + 
                "(SELECT COUNT(*) FROM viviendas) - COUNT(pagos_realizados.id) AS \"Cuotas No Pagadas\" " + 
                "FROM cuotas " + 
                "LEFT JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_cuota " + 
                "GROUP BY descripcion,fecha_emision, monto, fecha_limite");

        mapaTemporal.put("PagosRealizado", "SELECT viviendas.calle AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.descripcion AS Descripcion, " + 
                "pagos_realizados.tipo_pago AS \"Tipo de Pago\", " + 
                "pagos_realizados.referencia AS Referencia, " + 
                "pagos_realizados.fecha_de_pago AS \"Fecha de Pago\" " + 
                "FROM pagos_realizados " + 
                "JOIN cuotas ON cuotas.id = pagos_realizados.id_cuota " + 
                "JOIN viviendas ON viviendas.id = pagos_realizados.id_vivienda");

        mapaTemporal.put("Bitacora", "SELECT bitacoras.usuario AS Usuario, " + 
                "bitacoras.accion AS Accion, " + 
                "bitacoras.tabla_modificada AS \"Tabla Modificada\", " + 
                "bitacoras.fecha_modificacion AS \"Fecha de Modificacion\" " + 
                "FROM bitacoras");

        mapaTemporal.put("Lista de Morosos", "SELECT viviendas.calle,viviendas.numero_vivienda, " + //
                        "( " + //
                        "SELECT CONCAT(representantes.nombre,' ',representantes.apellido) " + //
                        "FROM representantes " + //
                        "WHERE representantes.id_vivienda = viviendas.id " + //
                        "ORDER BY representantes.id " + //
                        "LIMIT 1 " + //
                        ") as Representante, " + //
                        "solvencia(viviendas.id) AS Estado, " + //
                        "deuda(viviendas.id) AS Debe " + //
                        "FROM viviendas ");

        CONSULTAS = Collections.unmodifiableMap(mapaTemporal);
    }

//#endregion
    
//#region FRONTEND

    //Modulo QUERY SELECTOR
    Map<String,String> TableHeader = new HashMap<>();
    JComboBox<String> Modulos;

    //Visible SELECT
    JPanel isVisiblePanel = ThemeManager.Panel(new GridBagLayout()); 
    ArrayList<JCheckBox> isVisible = new ArrayList<>();  

    //Condition WHERE
    JComboBox<String> ColumnConditioSelector = new JComboBox<>();
    JPanel ConditionPanel = ThemeManager.Panel(new GridBagLayout());
    ArrayList<Input> Condition = new ArrayList<>();    
    
    //Sort ORDER BY
    JComboBox<String> OrderColumn,OrderBy;

    //Limit LIMIT
    JSpinner LimitFrom,LimitTo;
    
    //Table
    DefaultTableModel DATA = new DefaultTableModel(new String[][]{}, new String[]{}){
        @Override
        public boolean isCellEditable(int row,int column){return false;}
    };

    public MenuReporte() throws SQLException{
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        JTabbedPane Menu = new JTabbedPane();
        Menu.addTab("Modulo",Modulo());
        Menu.addTab( "Filtros",Filtros());
        Menu.addTab("Imprimir",Imprimir());

        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,Menu,Preview()),BorderLayout.CENTER);

        //UPDATES
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {SearchSQL();}
        });

        ChangeModule("Vivienda");
        SearchSQL();
    }

    JPanel Preview(){
        JPanel newPanel = ThemeManager.Panel(new BorderLayout());
        newPanel.add(ThemeManager.ScrollPanel(ThemeManager.Table(DATA)),BorderLayout.CENTER);
        return newPanel;
    } 

    JPanel Modulo(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);

        JLabel modulos = ThemeManager.Label("Modulo");
        newPanel.add(modulos,gbc);

        gbc.gridx=1;
        Modulos = ThemeManager.StringComboBox();
        for(String k : CONSULTAS.keySet()){Modulos.addItem(k);}  
        Modulos.setSelectedItem("Vivienda");
        newPanel.add(Modulos,gbc);
        Modulos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {ChangeModule(Modulos.getSelectedItem().toString());
                } catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridx=0; gbc.gridy=1;
        gbc.gridwidth=2; gbc.weightx=1;gbc.weighty=1;
        newPanel.add(new JScrollPane(isVisiblePanel),gbc);

        return newPanel;
    }

    JPanel Filtros(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);

        JLabel modulos = ThemeManager.Label("Columna");
        newPanel.add(modulos,gbc);

        gbc.gridx=1;gbc.weightx=1;
        ColumnConditioSelector = ThemeManager.StringComboBox();
        newPanel.add(ColumnConditioSelector,gbc);

        gbc.gridx=2;gbc.weightx=0;
        JButton BtnAgregar = ThemeManager.Button("+");
        newPanel.add(BtnAgregar,gbc);
        BtnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCondition(ColumnConditioSelector.getSelectedItem().toString());
            }
        });
        
        gbc.gridx=0; gbc.gridy=1;
        gbc.gridwidth=3; gbc.weightx=1;gbc.weighty=1;
        gbc.fill = GridBagConstraints.BOTH;
        newPanel.add(new JScrollPane(ConditionPanel),gbc);

        return newPanel;
    }

    JPanel Imprimir(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0; gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);    

        JLabel ordenar = ThemeManager.Label("Ordenar de manera");
        newPanel.add(ordenar,gbc);   

        gbc.gridx=1; 
        OrderColumn = ThemeManager.StringComboBox();
        newPanel.add(OrderColumn,gbc);

        gbc.gridx=2;     
        String OrderByValues[] = {"Ascendente","Descendente"};
        OrderBy = ThemeManager.StringComboBox();
        for(String k : OrderByValues){OrderBy.addItem(k);}          
        newPanel.add(OrderBy,gbc);

        gbc.gridy=1; gbc.gridx=0;
        JLabel limite = ThemeManager.Label("Limite");
        newPanel.add(limite,gbc);    
        
        gbc.gridx=1; 
        LimitFrom = new JSpinner( new SpinnerNumberModel(0, 0, 1000, 1));
        newPanel.add(LimitFrom,gbc);

        gbc.gridx=2; 
        LimitTo = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        newPanel.add(LimitTo,gbc);

        gbc.gridy=2;gbc.gridx=0; gbc.gridwidth=3;
        JButton BtnImprimir =  ThemeManager.Button("Exportar a PDF");
        newPanel.add(BtnImprimir,gbc);
        BtnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImprimirPDF();
            }
        });
 
        gbc.gridy=3; gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        newPanel.add(new JLabel(),gbc);

        return newPanel;
    }

//#endregion

//#region BACKEND
    void ChangeModule(String Module) throws SQLException{
        for (Component C : isVisiblePanel.getComponents()) {isVisiblePanel.remove(C);}
        for (Component C : ConditionPanel.getComponents()) {ConditionPanel.remove(C);}
        TableHeader.clear();
        isVisible.clear();
        Condition.clear();

        String SQL_query = CONSULTAS.get(Module);
        String TempView = "CREATE OR REPLACE TEMP VIEW vista_analisis_columnas AS " + SQL_query;
        DB.comandoDML(TempView, new Object[]{});

        String SQL_HEADER = "SELECT column_name AS columna_nombre, data_type AS tipo_base " + 
                            "FROM information_schema.columns " + 
                            "WHERE table_name = 'vista_analisis_columnas' " + 
                            "ORDER BY ordinal_position;";

        ResultSet RS_DATA = DB.consultar(SQL_HEADER, null);
        
        TempView = "DROP VIEW vista_analisis_columnas;";
        DB.comandoDML(TempView, new Object[]{});

        while (RS_DATA.next()) {            
            TableHeader.put(RS_DATA.getString("columna_nombre"), 
                            RS_DATA.getString("tipo_base"));
        }

        //View

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;
        gbc.gridy = 0; 

        for(String k : TableHeader.keySet()){
            JCheckBox newCheck = new JCheckBox(k);
            isVisiblePanel.add(newCheck,gbc);
            isVisible.add(newCheck);
            gbc.gridy++;

            newCheck.setSelected(true);
            newCheck.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            newCheck.setForeground(ThemeManager.COLOR_TEXT);
            newCheck.setFont(ThemeManager.TEXT_NORMAL);
        }

        gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        isVisiblePanel.add(new JLabel(),gbc);

        //Filtros 
        ColumnConditioSelector.removeAllItems();
        for(String k : TableHeader.keySet()){ColumnConditioSelector.addItem(k);}   
        
        //Imprimir
        OrderColumn.removeAllItems();
        for(String k : TableHeader.keySet()){OrderColumn.addItem(k);}   
 
        this.revalidate();
        this.repaint();
    }

    void AddCondition(String Column){
        if(ConditionPanel.getComponentCount() != 0){ConditionPanel.remove(ConditionPanel.getComponentCount()-1);}

        Input tempCondition = null;
        switch (TableHeader.get(Column)) {
            case "character varying": tempCondition = new StringInput(Column); break;
            case "bigint": tempCondition = new IntegerInput(Column); break;
            case "numeric": tempCondition = new DecimalInput(Column); break;
            case "timestamp without time zone": tempCondition = new DateInput(Column); break;
            default: tempCondition = new StringInput(Column); break;
        }

        final Input newCondition = tempCondition;
        newCondition.BtnRemover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Condition.remove(newCondition);
                ConditionPanel.remove(newCondition);
                ConditionPanel.revalidate();
                ConditionPanel.repaint();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0; gbc.gridy = Condition.size();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        ConditionPanel.add(newCondition,gbc);
        Condition.add(newCondition);
        
        gbc.gridy++;
        gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        ConditionPanel.add(new JLabel(),gbc);
        this.revalidate();
        this.repaint();
    }

    void SearchSQL(){
        //System.out.print("\033[H\033[2J");
        //System.out.flush();
        //System.out.println("Searching...");

        String SELECT = "SELECT ";
        ArrayList<String> isVList = new ArrayList<>();
        for (JCheckBox C : isVisible) {
            if(!C.isSelected()){continue;}
            isVList.add("\""+C.getText()+"\"");
        }
        if (isVList.size()==0) {JOptionPane.showMessageDialog(this, "DEBE MOSTRAR AL MENOS UNA COLUMNA");return;}
        SELECT = SELECT + String.join(", ", isVList);

        String WHERE = "WHERE ";
        ArrayList<String> cond = new ArrayList<>();
        for (Input C : Condition) {
            System.out.println(C.getClass().getName());
            if(C.GetInput()==""){continue;}
            if(C.GetInput()=="???"){return;}
            cond.add(C.GetInput());
        }WHERE = WHERE + String.join(" AND ", cond);
        if(cond.size()==0){WHERE="";}

        String ORDER_BY = "ORDER BY "+ OrderColumn.getSelectedItem();
        if(OrderBy.getSelectedItem()=="Ascendente"){ORDER_BY += " ASC";}
        else{ORDER_BY += " DESC";}

        String LIMIT = "LIMIT "+ LimitTo.getValue().toString();
        String OFFSET = "OFFSET "+ LimitFrom.getValue().toString();

        String MAIN_QUERY = SELECT+" FROM ("+ CONSULTAS.get(Modulos.getSelectedItem().toString()) +") as xd "+WHERE+" "+ORDER_BY+" "+LIMIT+" "+OFFSET;
        //System.out.println(MAIN_QUERY);

        try {DATA.setDataVector(GetData(MAIN_QUERY,isVList), isVList.toArray());
        } catch (SQLException e1) {e1.printStackTrace();}
        this.repaint();
        this.revalidate();
    }

    String[][] GetData(String SQL,ArrayList<String> header) throws SQLException{
        ResultSet RS_DATA = DB.consultar(SQL, null);
        
        //Getting Data
        ArrayList<ArrayList<String>> Datas = new ArrayList<>();
        while (RS_DATA.next()) {            
            ArrayList<String> newData = new ArrayList<>();
            for(String h: header){newData.add(RS_DATA.getString(h.substring(1,h.length()-1)));}
            Datas.add(newData);
        }

        //Converting...
        String[][] result = new String[Datas.size()][];
        for (int i = 0; i < Datas.size(); i++) {
            ArrayList<String> row = Datas.get(i);
            result[i] = row.toArray(new String[0]);
        }

        return result;
    }
//#endregion

//#region EXPORTAR PDF
    public void ImprimirPDF() {
        // 1. Crear el objeto Documento
        Document documento = new Document();        
        try {
            String rutaDescargas = System.getProperty("user.home") + 
                                java.io.File.separator + "Downloads" + java.io.File.separator + 
                                "Reporte Garita -"+ Modulos.getSelectedItem().toString()+".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(rutaDescargas));
            documento.open();

            Paragraph titulo = new Paragraph("Reporte Garita -"+ Modulos.getSelectedItem().toString());
            //titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            float[] anchosColumnas = new float[DATA.getColumnCount()];
            for (int i = 0; i < DATA.getColumnCount(); i++) {anchosColumnas[i] = 1f;}
            PdfPTable tabla = new PdfPTable(anchosColumnas);
            tabla.setWidthPercentage(100); // Que ocupe el 100% del ancho de la página

            String[] encabezados = new String[DATA.getColumnCount()];
            for (int i = 0; i < DATA.getColumnCount(); i++) {encabezados[i] = DATA.getColumnName(i);}
            
        
            String[][] datos = new String[DATA.getRowCount()][DATA.getColumnCount()];  
            for (int i = 0; i < DATA.getRowCount(); i++) {
                for (int j = 0; j < DATA.getColumnCount(); j++) {
                    Object valor = DATA.getValueAt(i, j);
                    datos[i][j] = (valor != null) ? valor.toString() : "";
                }
            }           

            for (String textoHeader : encabezados) {
                PdfPCell celdaHeader = new PdfPCell(new Phrase(textoHeader));
                celdaHeader.setBackgroundColor(ThemeManager.COLOR_PRIMARY); // Color de fondo azul
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
            System.out.println("¡PDF creado con éxito con OpenPDF!");

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }
//#endregion
}
