package Frontend.Reportes;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Backend.*;
import Backend.BDD.DataInputs.*;

public class ReporteAvanzado extends JPanel {
    ConexionPostgres DB = new ConexionPostgres();

//#region SQL QUERY
    Map<String, String> CONSULTAS = Map.ofEntries(
        Map.entry("Vivienda", "SELECT viviendas.categoria AS Calle, " +
                "viviendas.numero_vivienda AS Vivienda, " +
                "COUNT(representantes.id) AS Representantes, " +
                "COUNT(carnets.id) AS Carnets " +
                "FROM viviendas " +
                "JOIN representantes ON viviendas.id = representantes.id_vivienda " +
                "JOIN carnets ON viviendas.id = carnets.id_vivienda " +
                "GROUP BY viviendas.categoria, viviendas.numero_vivienda"),

        Map.entry("Vecinos", "SELECT viviendas.categoria AS Calle, " +
                "viviendas.numero_vivienda AS Vivienda, " +
                "representantes.nombre AS Nombre, " +
                "representantes.apellido AS Apellido, " +
                "representantes.cedula AS Cedula, " +
                "representantes.telefono AS Telefono " +
                "FROM representantes " +
                "JOIN viviendas ON viviendas.id = representantes.id_vivienda "),

        Map.entry("Carnets", "SELECT viviendas.categoria AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " +
                "carnets.codigo AS Codigo " + 
                "FROM carnets " +
                "JOIN viviendas ON viviendas.id = carnets.id_vivienda"),

        Map.entry("RegistrosAcceso", "SELECT accesos.fecha_hora AS \"Fecha de Acceso\"," + 
                " accesos.tipo AS Tipo, " + 
                " accesos.estado AS Estado, " + 
                " viviendas.categoria AS Calle, " + 
                " viviendas.numero_vivienda AS Vivienda, " + 
                " carnets.codigo AS Codigo, " + 
                " accesos.nombre_visita AS Visita " + 
                "FROM accesos " + 
                "LEFT JOIN carnets ON carnets.id = accesos.id_carnet " + 
                "LEFT JOIN viviendas ON viviendas.id = carnets.id_vivienda"),

        Map.entry("Cuotas", "SELECT cuotas.descripcion AS Descripcion, " + 
                "cuotas.fecha_emision AS \"Fecha de Emision\", " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.fecha_limite AS \"Fecha Limite de pago\", " + 
                "COUNT(pagos_realizados.id) AS \"Cuotas Pagadas\", " + 
                "(SELECT COUNT(*) FROM viviendas) - COUNT(pagos_realizados.id) AS \"Cuotas No Pagadas\" " + 
                "FROM cuotas " + 
                "LEFT JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_cuota " + 
                "GROUP BY descripcion,fecha_emision, monto, fecha_limite"),

        Map.entry("PagosRealizado", "SELECT viviendas.categoria AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.descripcion AS Descripcion, " + 
                "pagos_realizados.tipo_pago AS \"Tipo de Pago\", " + 
                "pagos_realizados.referencia AS Referencia, " + 
                "pagos_realizados.fecha_de_pago AS \"Fecha de Pago\" " + 
                "FROM pagos_realizados " + 
                "JOIN cuotas ON cuotas.id = pagos_realizados.id_cuota " + 
                "JOIN viviendas ON viviendas.id = pagos_realizados.id_vivienda"),

        Map.entry("Bitacora", "SELECT bitacoras.usuario AS Usuario, " + 
                "bitacoras.accion AS Accion, " + 
                "bitacoras.tabla_modificada AS \"Tabla Modificada\", " + 
                "bitacoras.fecha_modificacion AS \"Fecha de Modificacion\" " + 
                "FROM bitacoras")
    );

//#endregion
    
//#region FRONTEND

    //Modulo QUERY SELECTOR
    Map<String,String> TableHeader = new HashMap<>();
    JComboBox<String> Modulos;

    //Visible SELECT
    JPanel isVisiblePanel = new JPanel(new GridBagLayout()); 
    ArrayList<JCheckBox> isVisible = new ArrayList<>();  

    //Condition WHERE
    JComboBox<String> ColumnConditioSelector = new JComboBox<>();
    JPanel ConditionPanel = new JPanel(new GridBagLayout());
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

    public ReporteAvanzado() throws SQLException{
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
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(new JScrollPane(new JTable(DATA)),BorderLayout.CENTER);
        return newPanel;
    } 

    JPanel Modulo(){
        JPanel newPanel =  new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);

        JLabel modulos = new JLabel("Modulo:");
        newPanel.add(modulos,gbc);
        modulos.setFont(ThemeManager.TEXT_SUBTITLE);
        modulos.setForeground(ThemeManager.COLOR_TEXT);

        gbc.gridx=1;
        Modulos = new JComboBox<>();
        for(String k : CONSULTAS.keySet()){Modulos.addItem(k);}  
        Modulos.setSelectedItem("Vivienda");
        newPanel.add(Modulos,gbc);
        Modulos.setFont(ThemeManager.TEXT_NORMAL);
        Modulos.setForeground(ThemeManager.COLOR_TEXT_DARK);
        Modulos.setBackground(ThemeManager.COLOR_SECONDARY);
        Modulos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {ChangeModule(Modulos.getSelectedItem().toString());
                } catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridx=0; gbc.gridy=1;
        gbc.gridwidth=2; gbc.weightx=1;gbc.weighty=1;
        isVisiblePanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        newPanel.add(new JScrollPane(isVisiblePanel),gbc);

        return newPanel;
    }

    JPanel Filtros(){
        JPanel newPanel =  new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);

        JLabel modulos = new JLabel("Columna:");
        newPanel.add(modulos,gbc);
        modulos.setFont(ThemeManager.TEXT_SUBTITLE);
        modulos.setForeground(ThemeManager.COLOR_TEXT);

        gbc.gridx=1;gbc.weightx=1;
        ColumnConditioSelector = new JComboBox<String>();
        newPanel.add(ColumnConditioSelector,gbc);
        ColumnConditioSelector.setFont(ThemeManager.TEXT_NORMAL);
        ColumnConditioSelector.setForeground(ThemeManager.COLOR_TEXT_DARK);
        ColumnConditioSelector.setBackground(ThemeManager.COLOR_SECONDARY);

        gbc.gridx=2;gbc.weightx=0;
        JButton BtnAgregar = new JButton("+");
        newPanel.add(BtnAgregar,gbc);
        BtnAgregar.setFont(ThemeManager.TEXT_NORMAL);
        BtnAgregar.setForeground(ThemeManager.COLOR_TEXT);
        BtnAgregar.setBackground(ThemeManager.COLOR_PRIMARY);
        BtnAgregar.setFont(ThemeManager.TEXT_SUBTITLE);
        BtnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCondition(ColumnConditioSelector.getSelectedItem().toString());
            }
        });
        BtnAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnAgregar.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnAgregar.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });
        
        gbc.gridx=0; gbc.gridy=1;
        gbc.gridwidth=3; gbc.weightx=1;gbc.weighty=1;
        gbc.fill = GridBagConstraints.BOTH;
        ConditionPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        newPanel.add(new JScrollPane(ConditionPanel),gbc);

        return newPanel;
    }

    JPanel Imprimir(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0; gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);    

        JLabel ordenar = new JLabel(" Ordenar de Manera:");
        ordenar.setFont(ThemeManager.TEXT_NORMAL);
        ordenar.setForeground(ThemeManager.COLOR_TEXT);
        newPanel.add(ordenar,gbc);   

        gbc.gridx=1; 
        OrderColumn = new JComboBox<>();
        newPanel.add(OrderColumn,gbc);

        gbc.gridx=2;     
        String OrderByValues[] = {"Ascendente","Descendente"};
        OrderBy = new JComboBox<>(OrderByValues);
        newPanel.add(OrderBy,gbc);

        gbc.gridy=1; gbc.gridx=0;
        JLabel limite = new JLabel("Limite:");
        limite.setFont(ThemeManager.TEXT_NORMAL);
        limite.setForeground(ThemeManager.COLOR_TEXT);
        newPanel.add(limite,gbc);    
        
        gbc.gridx=1; 
        LimitFrom = new JSpinner( new SpinnerNumberModel(0, 0, 1000, 1));
        newPanel.add(LimitFrom,gbc);

        gbc.gridx=2; 
        LimitTo = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        newPanel.add(LimitTo,gbc);

        gbc.gridy=2;gbc.gridx=0; gbc.gridwidth=3;
        JButton BtnImprimir = new JButton("Exportar a PDF");
        BtnImprimir.setFont(ThemeManager.TEXT_NORMAL);
        BtnImprimir.setForeground(ThemeManager.COLOR_TEXT);
        BtnImprimir.setBackground(ThemeManager.COLOR_PRIMARY);
        BtnImprimir.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(BtnImprimir,gbc);

        BtnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImprimirPDF();
            }
        });
        BtnImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnImprimir.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnImprimir.setBackground(ThemeManager.COLOR_PRIMARY);
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
            case "timestamp with time zone": tempCondition = new DateInput(Column); break;
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("Searching...");

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
            cond.add(C.GetInput());
        }if(cond.size()==0){WHERE="";}

        String ORDER_BY = "ORDER BY "+ OrderColumn.getSelectedItem();
        if(OrderBy.getSelectedItem()=="Ascendente"){ORDER_BY += " ASC";}
        else{ORDER_BY += " DESC";}

        String LIMIT = "LIMIT "+ LimitTo.getValue().toString();
        String OFFSET = "OFFSET "+ LimitFrom.getValue().toString();

        String MAIN_QUERY = SELECT+" FROM ("+ CONSULTAS.get(Modulos.getSelectedItem().toString()) +") "+WHERE+" "+ORDER_BY+" "+LIMIT+" "+OFFSET;

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
    void ImprimirPDF(){
        System.out.println("Proximamente...");
    }
//#endregion
}
