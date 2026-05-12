package Reportes;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import BDD.ConexionPostgres;
import BDD.DataInputs.*;

public class ReporteGeneral extends JPanel{
    ConexionPostgres DB = new ConexionPostgres();

    String queryTable = "SELECT table_name \n" +
                    "FROM information_schema.tables \n" +
                    "WHERE table_schema = 'public' \n" +
                    "AND table_type = 'BASE TABLE'";
    
    String queryColumns = "SELECT column_name, data_type, character_maximum_length,is_nullable \n" +
                        "FROM  information_schema.columns \n" +
                        "WHERE table_name = ?";  

    ArrayList<String> TABLES = new ArrayList<>();
    ArrayList<ArrayList<String>> COLMUNS = new ArrayList<>();

    //CENTER PANEL
    JComboBox<Object> TableMenu;
    DefaultTableModel DataTable = new DefaultTableModel(new String[][]{}, new String[]{});
    ArrayList<JPanel> Columns = new ArrayList<>();

    //BOTTOM PANEL
    JComboBox<String> OrdernarColumnasMenu,OrdernarMenu;
    JSpinner LimitInput;

    public ReporteGeneral() throws SQLException{
        InitSearch();
        this.setLayout(new BorderLayout());
        this.add(Inspector(),BorderLayout.WEST);
        this.add(Preview(),BorderLayout.CENTER);


        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("SEARCHING...");
                GetWhereClauses();
                try {DataTable.setDataVector(SQL_SEARCH(), COLMUNS.get(TableMenu.getSelectedIndex()).toArray());
                } catch (SQLException e1) {e1.printStackTrace();}}
        });
    }
    
    public void InitSearch() throws SQLException{
        ResultSet RS_TABLE = DB.consultar(queryTable, null);
        while (RS_TABLE.next()) {
            String Table = RS_TABLE.getString("table_name");
            TABLES.add(Table);
            
            ArrayList<String> newColumns = new ArrayList<>();
            ResultSet RS_COLUMNS = DB.consultar(queryColumns, new Object[]{Table});
            while (RS_COLUMNS.next()) {   
                String str =  RS_COLUMNS.getString("column_name")+" - "+RS_COLUMNS.getString("data_type").split(" ")[0];
                newColumns.add(str);
            }  
            COLMUNS.add(newColumns);
        }
    }
  
    
    JPanel Inspector(){
       JPanel newPanel = new JPanel();

        JPanel CenterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel SouthPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        JScrollPane Scroll = new JScrollPane(CenterPanel);

        newPanel.setLayout(new BorderLayout());
        newPanel.add(new JLabel("Filtros - Presione ENTER para actualizar la vista previa"),BorderLayout.NORTH);
        newPanel.add(Scroll,BorderLayout.CENTER);
        newPanel.add(SouthPanel,BorderLayout.SOUTH);

        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        JLabel tablas = new JLabel("Tablas:");
        CenterPanel.add(tablas,gbc);
        
        gbc.gridx=1; gbc.weightx=1;
        TableMenu = new JComboBox<>(TABLES.toArray());
        CenterPanel.add(TableMenu,gbc);

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

                    for (String C : COLMUNS.get(TableMenu.getSelectedIndex())) {
                        OrdernarColumnasMenu.addItem(C.split(" - ")[0]);
                        
                        switch (C.split(" - ")[1]) {
                            case "integer":
                                Columns.add(new IntegerInput(C.split(" - ")[0]));
                                break;

                            case "numeric":
                                Columns.add(new DecimalInput(C.split(" - ")[0]));
                                break;

                            case "character":
                                Columns.add(new StringInput(C.split(" - ")[0]));
                                break;

                            case "timestamp":
                                Columns.add(new DateInput(C.split(" - ")[0]));    
                                break;

                            case "boolean":
                                Columns.add(new BooleanInput(C.split(" - ")[0]));    
                                break;
                        }

                        gbc.gridy=gbc.gridy+1;                     
                        CenterPanel.add(Columns.get(Columns.size()-1),gbc);
                    }
                    
                    try {DataTable.setDataVector(SQL_SEARCH(), COLMUNS.get(TableMenu.getSelectedIndex()).toArray());
                    } catch (SQLException e1) {e1.printStackTrace();}

                    CenterPanel.revalidate();
                    CenterPanel.repaint();
                }
            }
        );

        gbc.gridx=0;gbc.gridwidth=2;
        gbc.insets = new Insets(10,10,10,10);

        gbc2.gridy=0;gbc2.gridx=0;
        gbc2.fill=GridBagConstraints.BOTH;
        gbc2.insets = new Insets(5,10,5,10);    

        gbc2.gridy=1;
        JLabel ordenar = new JLabel(" Ordenar de Manera:");
        SouthPanel.add(ordenar,gbc2);   

        gbc2.gridy=2;
        JLabel limite = new JLabel("Limite:");
        SouthPanel.add(limite,gbc2);    

        gbc2.gridy=1;
        
        gbc2.gridx=1; gbc2.gridwidth=1;
        OrdernarColumnasMenu = new JComboBox<>();
        SouthPanel.add(OrdernarColumnasMenu,gbc2);
     
        gbc2.gridx=2;gbc2.weightx=0;
        String OrderBy[] = {"ASC","DESC"};
        OrdernarMenu = new JComboBox<>(OrderBy);
        SouthPanel.add(OrdernarMenu,gbc2);

        gbc2.gridy=2;
        
        gbc2.gridx=1;gbc2.gridwidth=2; gbc2.weightx=1;
        LimitInput = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        SouthPanel.add(LimitInput,gbc2);

        gbc2.gridy=3;

        gbc2.gridx=0; gbc2.gridwidth=3;
        JButton BtnImprimir = new JButton("Imprimir");
        SouthPanel.add(BtnImprimir,gbc2);

        return newPanel;
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BorderLayout());

        JTable table = new JTable(DataTable);
        JScrollPane Scroll = new JScrollPane(table);

        newPanel.add(new JLabel(" "),BorderLayout.NORTH);
        newPanel.add(Scroll,BorderLayout.CENTER);

        return newPanel;
    } 

//#region SQL RESEARCH
    String[][] SQL_SEARCH() throws SQLException{
        String SQL = "SELECT ";

        for(Object C: COLMUNS.get(TableMenu.getSelectedIndex()).toArray()){
            SQL += C.toString() .split(" - ")[0]+ ", ";
        }

        SQL = SQL.substring(0,SQL.length()-2) + " ";
        SQL += "FROM "+ TableMenu.getSelectedItem().toString();

        ArrayList<String> Clauses = GetWhereClauses();
        if(Clauses.contains("???")){return new String[][]{};}

        if(!Clauses.isEmpty()){
            SQL += " WHERE " + Clauses.get(0);
            Clauses.remove(0);
            for(String C: Clauses){
                SQL += " AND " + C;
            } 
        }

        SQL += " ORDER BY " + OrdernarColumnasMenu.getSelectedItem().toString() + " " + OrdernarMenu.getSelectedItem().toString();
        SQL += " LIMIT " + LimitInput.getValue();

        System.out.println(SQL);
        ResultSet RS_DATA = DB.consultar(SQL, null);

        //Getting Data
        ArrayList<ArrayList<String>> Datas = new ArrayList<>();
        while (RS_DATA.next()) {            
            ArrayList<String> newData = new ArrayList<>();
            for(Object C: COLMUNS.get(TableMenu.getSelectedIndex()).toArray()){
                newData.add(RS_DATA.getString(C.toString().split(" - ")[0])) ;}
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
            String type = COLMUNS.get(TableMenu.getSelectedIndex()).get(i).split(" - ")[1];    
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