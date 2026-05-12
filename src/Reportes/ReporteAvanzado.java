package Reportes;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

import com.toedter.calendar.JDateChooser;
import BDD.ConexionPostgres;

public class ReporteAvanzado extends JPanel {
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

    public ReporteAvanzado() throws SQLException {
        InitSearch();
        this.setLayout(new BorderLayout());
        this.add(Inspector(),BorderLayout.WEST);
        this.add(Preview(),BorderLayout.CENTER);
    }

    public void InitSearch() throws SQLException{
        ResultSet RS_TABLE = DB.consultar(queryTable, null);
        while (RS_TABLE.next()) {
            String Table = RS_TABLE.getString("table_name");
            TABLES.add(Table);
            
            ArrayList<String> newColumns = new ArrayList<>();
            ResultSet RS_COLUMNS = DB.consultar(queryColumns, new Object[]{Table});
            while (RS_COLUMNS.next()) {    
                newColumns.add(RS_COLUMNS.getString("column_name")+" - "+RS_COLUMNS.getString("data_type").split(" ")[0]);
            }  
            COLMUNS.add(newColumns);
        }
    }

//#region SELECT WINDOWS

    JPanel Inspector(){
        JPanel newPanel = new JPanel();

        ArrayList<JPanel> Tables = new ArrayList<>();
        JPanel CenterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel SouthPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        JScrollPane Scroll = new JScrollPane(CenterPanel);

        newPanel.setLayout(new BorderLayout());
        newPanel.add(new JLabel("Filtros"),BorderLayout.NORTH);
        newPanel.add(Scroll,BorderLayout.CENTER);
        newPanel.add(SouthPanel,BorderLayout.SOUTH);

        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        JLabel tablas = new JLabel("Tablas:");
        CenterPanel.add(tablas,gbc);
        
        gbc.gridx=1; gbc.weightx=1;
        JComboBox TableMenu = new JComboBox<>(TABLES.toArray());
        CenterPanel.add(TableMenu,gbc);

        gbc.gridx=2; gbc.weightx=0;
        JButton BtnAddTable = new  JButton("+");
        CenterPanel.add(BtnAddTable,gbc);

        gbc.gridx=0;gbc.gridwidth=3;
        gbc.insets = new Insets(10,10,10,10);

        BtnAddTable.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Tables.add(TableManager(TableMenu.getSelectedIndex()));
                    gbc.gridy=gbc.gridy+1; 
                    
                    CenterPanel.add(Tables.get(Tables.size()-1),gbc);
                    
                    CenterPanel.revalidate();
                    CenterPanel.repaint();
                }
            }
        );

        gbc2.gridy=0;gbc2.gridx=0;
        gbc2.fill=gbc2.BOTH;
        gbc2.insets = new Insets(5,10,5,10);

        JLabel agrupar = new JLabel("Agrupar por:");
        SouthPanel.add(agrupar,gbc2);        

        gbc2.gridx=1; gbc2.weightx=1;
        JComboBox AgruparMenu = new JComboBox<>();
        SouthPanel.add(AgruparMenu,gbc2);

        gbc2.gridy=1;

        gbc2.gridx=0; gbc2.weightx=0;
        JLabel ordenar = new JLabel(" Ordenar de Manera:");
        SouthPanel.add(ordenar,gbc2);        

        gbc2.gridx=1; gbc2.weightx=1;
        String OrderBy[] = {"ASC","DEC"};
        JComboBox OrdernarMenu = new JComboBox<>(OrderBy);
        SouthPanel.add(OrdernarMenu,gbc2);

        gbc2.gridy=3;

        gbc2.gridx=0; gbc2.weightx=0;
        JLabel limite = new JLabel("Limite:");
        SouthPanel.add(limite,gbc2);        

        gbc2.gridx=1; gbc2.weightx=1;
        JTextField LimitInput = new JTextField();
        SouthPanel.add(LimitInput,gbc2);

        gbc2.gridy=4;

        gbc2.gridx=0; gbc2.gridwidth=2;
        JButton BtnImprimir = new JButton("Imprimir");
        SouthPanel.add(BtnImprimir,gbc2);

        return newPanel;
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BorderLayout());

        JScrollPane Scroll = new JScrollPane();
        newPanel.add(new JLabel("Vista Previa"),BorderLayout.NORTH);
        newPanel.add(Scroll,BorderLayout.CENTER);

        return newPanel;
    }

//#endregion

//#region FROM TABLE MANAGER
    JPanel TableManager(int LabelIndex){
        ArrayList<JPanel> Columns = new ArrayList<>();
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=gbc.BOTH;
        gbc.insets = new Insets(1,1,1,1);
        
        gbc.weightx=1;
        JLabel tableName = new JLabel(TABLES.get(LabelIndex));
        newPanel.add(tableName,gbc);

        gbc.gridx=1;gbc.weightx=0;
        JButton deleteBtn = new JButton("x");
        newPanel.add(deleteBtn,gbc);

        gbc.gridy=1;
        
        gbc.gridx=0;gbc.weightx=1;
        JComboBox ColumnsMenu = new JComboBox<>(COLMUNS.get(LabelIndex).toArray());
        newPanel.add(ColumnsMenu,gbc);

        gbc.gridx=1;gbc.weightx=0;
        JButton BtnAddColumn = new JButton("+");
        newPanel.add(BtnAddColumn,gbc);

        gbc.gridx=0;gbc.gridwidth=2;
        gbc.insets = new Insets(10,10,10,10);

        BtnAddColumn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String str = COLMUNS.get(LabelIndex).get(ColumnsMenu.getSelectedIndex());

                    switch (str.split(" - ")[1]) {
                        case "integer":
                            Columns.add(NumericInput(str.split(" - ")[0]));
                            break;

                        case "numeric":
                            Columns.add(NumericInput(str.split(" - ")[0]));
                            break;

                        case "character":
                            Columns.add(StringInput(str.split(" - ")[0]));
                            break;

                        case "timestamp":
                            Columns.add(DateInput(str.split(" - ")[0]));    
                            break;

                        case "boolean":
                            Columns.add(BooleanInput(str.split(" - ")[0]));    
                            break;
                    }

                    gbc.gridy=gbc.gridy+1;                     
                    newPanel.add(Columns.get(Columns.size()-1),gbc);
                    
                    newPanel.revalidate();
                    newPanel.repaint();
                }
            }
        );

        deleteBtn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Container parent = newPanel.getParent();
                    parent.remove(newPanel);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        );

        return newPanel;
    }

//#endregion

//#region WHERE INPUTS TEMPLATES
    JPanel NumericInput(String label){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=gbc.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        gbc.gridy=0;
        
        String Logics[] = {"AND","OR"};
        JComboBox<String> LogicsMenu = new JComboBox<>(Logics);
        newPanel.add(LogicsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;
        JLabel title = new JLabel(label);
        newPanel.add(title,gbc);

        gbc.gridx=2;gbc.weightx=0;
        JButton deleteBtn = new JButton("x");
        newPanel.add(deleteBtn,gbc);

        gbc.gridy=1;
        
        gbc.gridx=0;
        String Options[] = {"!=","=",">",">=","<=",};
        JComboBox<String> OptionsMenu = new JComboBox<>(Options);
        newPanel.add(OptionsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;gbc.gridwidth=2;
        JTextField input =  new JTextField();
        newPanel.add(input,gbc);

        gbc.gridy=2;

        gbc.gridx=0;gbc.weightx=0;gbc.gridwidth=1;
        JLabel retorna = new JLabel("Retorna:");
        newPanel.add(retorna,gbc);

        gbc.gridx=1;gbc.weightx=1;gbc.gridwidth=2;
        String Funtions[] = {"VALUE","SUM","COUNT","AVG","MIN","MAX"};
        JComboBox<String> FunctionMenu = new JComboBox<>(Funtions);
        newPanel.add(FunctionMenu,gbc);

        deleteBtn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Container parent = newPanel.getParent();
                    parent.remove(newPanel);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        );

        return newPanel;
    }

    JPanel StringInput(String label){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=gbc.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        gbc.gridy=0;
        
        String Logics[] = {"AND","OR"};
        JComboBox<String> LogicsMenu = new JComboBox<>(Logics);
        newPanel.add(LogicsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;
        JLabel title = new JLabel(label);
        newPanel.add(title,gbc);

        gbc.gridx=2;gbc.weightx=0;
        JButton deleteBtn = new JButton("x");
        newPanel.add(deleteBtn,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.gridwidth=1;
        String Options[] = {"!=","="};
        JComboBox<String> OptionsMenu = new JComboBox<>(Options);
        newPanel.add(OptionsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;gbc.gridwidth=2;
        JTextField input =  new JTextField();
        newPanel.add(input,gbc);

        deleteBtn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Container parent = newPanel.getParent();
                    parent.remove(newPanel);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        );

        return newPanel;
    }
    
    JPanel DateInput(String label){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.insets = new Insets(1,1,1,1);
        gbc.fill = gbc.BOTH;
        
        String Logics[] = {"AND","OR"};
        JComboBox<String> LogicsMenu = new JComboBox<>(Logics);
        newPanel.add(LogicsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;gbc.gridwidth=2;
        JLabel title = new JLabel(label);
        newPanel.add(title,gbc);

        gbc.gridx=3;gbc.weightx=0;gbc.gridwidth=1;
        JButton deleteBtn = new JButton("x");
        newPanel.add(deleteBtn,gbc);

        gbc.gridy=1;gbc.gridx=0;
        JLabel From = new JLabel("Desde");
        newPanel.add(From,gbc);

        gbc.gridy=2;gbc.gridx=0;
        JLabel To = new JLabel("Hasta");
        newPanel.add(To,gbc);
    
        gbc.gridy=1;gbc.weightx=1;
        
        gbc.gridx=1;
        JDateChooser FromDate = new JDateChooser();
        newPanel.add(FromDate,gbc);

        gbc.gridx=2;
        JSpinner FromTime = new JSpinner(new SpinnerDateModel());
        FromTime.setEditor(new JSpinner.DateEditor(FromTime, "HH:mm:ss"));
        newPanel.add(FromTime,gbc);
        
        gbc.gridy=2;
        
        gbc.gridx=1;
        JDateChooser ToDate = new JDateChooser();
        newPanel.add(ToDate,gbc);

        gbc.gridx=2;
        JSpinner ToTime = new JSpinner(new SpinnerDateModel());
        ToTime.setEditor(new JSpinner.DateEditor(ToTime, "HH:mm:ss"));
        newPanel.add(ToTime,gbc);

        deleteBtn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Container parent = newPanel.getParent();
                    parent.remove(newPanel);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        );

        return newPanel;
    }

    JPanel BooleanInput(String label){
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=gbc.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        gbc.gridy=0;
        
        String Logics[] = {"AND","OR"};
        JComboBox<String> LogicsMenu = new JComboBox<>(Logics);
        newPanel.add(LogicsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;
        JLabel title = new JLabel(label);
        newPanel.add(title,gbc);

        gbc.gridx=2;gbc.weightx=0;
        JButton deleteBtn = new JButton("x");
        newPanel.add(deleteBtn,gbc);

        gbc.gridy=1;
        
        gbc.gridx=0;
        String Options[] = {"!=","="};
        JComboBox<String> OptionsMenu = new JComboBox<>(Options);
        newPanel.add(OptionsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;gbc.gridwidth=2;
        String Values[] = {"true","false"};
        JComboBox<String> input = new JComboBox<>(Values);
        newPanel.add(input,gbc);

        deleteBtn.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Container parent = newPanel.getParent();
                    parent.remove(newPanel);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        );

        return newPanel;
    }

}

//#endregion