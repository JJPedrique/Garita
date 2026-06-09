package Frontend.Reportes;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

import Backend.ConexionPostgres;
import Backend.BDD.DataInputs.*;

public class ReporteGeneral extends JPanel{
    ConexionPostgres DB = new ConexionPostgres();

    String queryTable = "SELECT table_name \n" +
                    "FROM information_schema.tables \n" +
                    "WHERE table_schema = 'public' \n" +
                    "AND table_type = 'BASE TABLE'";
    
    String queryColumns = "SELECT column_name, data_type, character_maximum_length,is_nullable \n" +
                        "FROM  information_schema.columns \n" +
                        "WHERE table_name = ?";  

    Map<String,Map<String,String>> BDD_SCHEME = new LinkedHashMap<>();

    
    //CENTER PANEL
    JComboBox<Object> TableMenu;
    DefaultTableModel DataTable = new DefaultTableModel(new String[][]{}, new String[]{}){
        @Override
        public boolean isCellEditable(int row,int column){return false;}
    };
    ArrayList<JPanel> Columns = new ArrayList<>();

    //BOTTOM PANEL
    JComboBox<String> OrdernarColumnasMenu,OrdernarMenu;
    JSpinner LimitInput;

    public ReporteGeneral() throws SQLException{
        InitSearch();
        this.setLayout(new BorderLayout());
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,Inspector(),Preview()),BorderLayout.CENTER);




        //this.add(I,BorderLayout.WEST);
        //this.add(,BorderLayout.CENTER);

        //UPDATES
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GetWhereClauses();
                try {DataTable.setDataVector(SQL_SEARCH(), BDD_SCHEME.get(TableMenu.getSelectedItem()).keySet().toArray());
                } catch (SQLException e1) {e1.printStackTrace();}}
        });
    }
      
//#region UI
    JPanel Inspector(){
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(new JLabel("Filtros - Presione ENTER para actualizar la vista previa"),BorderLayout.NORTH);
        newPanel.add(new JScrollPane(Center()),BorderLayout.CENTER);
        newPanel.add(Bottom(),BorderLayout.SOUTH);
        return newPanel;
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(new JLabel(" "),BorderLayout.NORTH);
        newPanel.add(new JScrollPane(new JTable(DataTable)),BorderLayout.CENTER);
        return newPanel;
    } 

    JPanel Center(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        JLabel tablas = new JLabel("Tablas:");
        newPanel.add(tablas,gbc);
        
        gbc.gridx=1; gbc.weightx=1;
        TableMenu = new JComboBox<>(BDD_SCHEME.keySet().toArray());
        TableMenu.setSelectedItem(TableMenu.getSelectedItem());
        newPanel.add(TableMenu,gbc);

        gbc.gridx=0;gbc.gridwidth=2;
        gbc.insets = new Insets(10,10,10,10);

        TableMenu.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JPanel C : Columns) {
                        Container parent = C.getParent();
                        parent.remove(C);
                    }

                    OrdernarColumnasMenu.removeAllItems();
                    Columns.clear();

                    for (Map.Entry<String,String> C : BDD_SCHEME.get(TableMenu.getSelectedItem()).entrySet()) {
                        OrdernarColumnasMenu.addItem(C.getKey());
                        
                        switch (C.getValue()) {
                            case "integer":Columns.add(new IntegerInput(C.getKey()));break;
                            case "numeric":Columns.add(new DecimalInput(C.getKey()));break;
                            case "character":Columns.add(new StringInput(C.getKey()));break;
                            case "timestamp":Columns.add(new DateInput(C.getKey()));break;
                            case "boolean":Columns.add(new BooleanInput(C.getKey()));break;
                        }

                        gbc.gridy=gbc.gridy+1;                     
                        newPanel.add(Columns.get(Columns.size()-1),gbc);
                    }
                    
                    try {DataTable.setDataVector(SQL_SEARCH(), BDD_SCHEME.get(TableMenu.getSelectedItem()).keySet().toArray());
                    } catch (SQLException e1) {e1.printStackTrace();}

                    newPanel.revalidate();
                    newPanel.repaint();
                }
            }
        );
        return newPanel;
    }

    JPanel Bottom(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,10,5,10);    

        JLabel ordenar = new JLabel(" Ordenar de Manera:");
        newPanel.add(ordenar,gbc);   

        gbc.gridx=1; 
        OrdernarColumnasMenu = new JComboBox<>();
        newPanel.add(OrdernarColumnasMenu,gbc);

        gbc.gridx=2;     
        String OrderBy[] = {"ASC","DESC"};
        OrdernarMenu = new JComboBox<>(OrderBy);
        newPanel.add(OrdernarMenu,gbc);

        gbc.gridy=1; gbc.gridx=0;
        JLabel limite = new JLabel("Limite:");
        newPanel.add(limite,gbc);    
        
        gbc.gridx=1;gbc.gridwidth=2; gbc.weightx=1;
        LimitInput = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        newPanel.add(LimitInput,gbc);

        gbc.gridy=2;gbc.gridx=0; gbc.gridwidth=3;
        JButton BtnImprimir = new JButton("Exportar a PDF");
        newPanel.add(BtnImprimir,gbc);
        
        return newPanel;
    }

//#endregion

//#region SQL
    public void InitSearch() throws SQLException{
        ResultSet RS_TABLE = DB.consultar(queryTable, null);
        while (RS_TABLE.next()) {
            String Table = RS_TABLE.getString("table_name");
    
            
            ResultSet RS_COLUMNS = DB.consultar(queryColumns, new Object[]{Table});
            Map<String, String> Columns = new LinkedHashMap<>();
            while (RS_COLUMNS.next()) {   
                String newColumn =RS_COLUMNS.getString("column_name");
                if(newColumn.substring(0,2).equals("id") || newColumn.equals("activo") ){continue;}
                Columns.put(newColumn,RS_COLUMNS.getString("data_type").split(" ")[0]);
            }  

            BDD_SCHEME.put(Table,Columns);
        }
    }

    String[][] SQL_SEARCH() throws SQLException{
        String SQL = "SELECT ";
        SQL += String.join(",",BDD_SCHEME.get(TableMenu.getSelectedItem()).keySet());

        SQL += " FROM "+ TableMenu.getSelectedItem();

        ArrayList<String> Clauses = GetWhereClauses();
        if(Clauses.contains("???")){return new String[][]{};}

        if(!Clauses.isEmpty()){SQL += " WHERE " + String.join(" AND ",Clauses);}
        SQL += " ORDER BY " + OrdernarColumnasMenu.getSelectedItem().toString() + " " + OrdernarMenu.getSelectedItem().toString();
        SQL += " LIMIT " + LimitInput.getValue();

        System.out.println(SQL);
        ResultSet RS_DATA = DB.consultar(SQL, null);
        
        //Getting Data
        ArrayList<ArrayList<String>> Datas = new ArrayList<>();
        while (RS_DATA.next()) {            
            ArrayList<String> newData = new ArrayList<>();
            for(String C: BDD_SCHEME.get(TableMenu.getSelectedItem()).keySet()){
                newData.add(RS_DATA.getString(C));}
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

    ArrayList<String> GetWhereClauses(){
        ArrayList<String> Clauses = new ArrayList<>();
        
        for(int i= 0; i< Columns.size();i++){
            String DAT="";
            
            String type = BDD_SCHEME.get(TableMenu.getSelectedItem()).get(Columns.get(i).getToolTipText());    
            switch (type) {
                case "integer":DAT=((IntegerInput)Columns.get(i)).GetInput();break;
                case "numeric":DAT=((DecimalInput)Columns.get(i)).GetInput();break;
                case "character":DAT=((StringInput)Columns.get(i)).GetInput();break;
                case "timestamp":DAT=((DateInput)Columns.get(i)).GetInput();break;
                case "boolean":DAT=((BooleanInput)Columns.get(i)).GetInput();break;
            }
        
            if(DAT.isEmpty()){continue;}
            Clauses.add(DAT);
        }
        
        return Clauses;
    }
//#endregion
}