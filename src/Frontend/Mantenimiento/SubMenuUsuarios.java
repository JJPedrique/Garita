package Frontend.Mantenimiento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SubMenuUsuarios extends JPanel {
    ConexionPostgres DB = new ConexionPostgres();

    String SQL = "SELECT concat(nombre,' ',apellido) AS \"Nombre Completo\", Cedula, telefono FROM usuarios";
    JTextField inputNombre =  new JTextField();
    JTextField inputCedula =  new JTextField();

    //Table
    DefaultTableModel DATA = new DefaultTableModel(new String[][]{}, new String[]{}){
        @Override
        public boolean isCellEditable(int row,int column){return false;}
    };

    public SubMenuUsuarios(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,Filtros(),Preview()),BorderLayout.CENTER);
        Search();

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(new JScrollPane(new JTable(DATA)),BorderLayout.CENTER);
        return newPanel;
    } 

    JPanel Filtros(){
        JPanel newPanel =  new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth=2;

        JButton AddUser = new JButton("Agregar Nuevo Usuario");
        AddUser.setFont(ThemeManager.TEXT_NORMAL);
        AddUser.setForeground(ThemeManager.COLOR_TEXT);
        AddUser.setBackground(ThemeManager.COLOR_PRIMARY);
        AddUser.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(AddUser,gbc);

        AddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println();
            }
        });
        AddUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                AddUser.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                AddUser.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });

        gbc.gridy=1;
        JLabel Filtros = new JLabel("Busqueda y Filtros"); 
        Filtros.setForeground(ThemeManager.COLOR_TEXT);       
        Filtros.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.gridy=2;gbc.weightx=0;

        JLabel Nombre = new JLabel("Nombre");   
        Nombre.setForeground(ThemeManager.COLOR_TEXT);     
        Nombre.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(Nombre,gbc);
        
        gbc.gridx=1;gbc.weightx=1;
        inputNombre =  new JTextField();
        inputNombre.setFont(ThemeManager.TEXT_NORMAL);
        inputNombre.setForeground(ThemeManager.COLOR_TEXT);
        inputNombre.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(inputNombre,gbc);


        gbc.gridy=3;gbc.gridx=0;gbc.weightx=0;
        JLabel Cedula = new JLabel("Cedula");  
        Cedula.setForeground(ThemeManager.COLOR_TEXT);      
        Cedula.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(Cedula,gbc);

        gbc.gridx=1;gbc.weightx=1;
        inputCedula =  new JTextField();
        inputCedula.setFont(ThemeManager.TEXT_NORMAL);
        inputCedula.setForeground(ThemeManager.COLOR_TEXT);
        inputCedula.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(inputCedula,gbc);



        gbc.gridwidth=2;gbc.gridy=4;gbc.gridx=0;  
        JButton Buscar = new JButton("Buscar");
        Buscar.setFont(ThemeManager.TEXT_NORMAL);
        Buscar.setForeground(ThemeManager.COLOR_TEXT);
        Buscar.setBackground(ThemeManager.COLOR_PRIMARY);
        Buscar.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(Buscar,gbc);

        Buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Buscar");
                Search();
            }
        });
        Buscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Buscar.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Buscar.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });

        gbc.gridy=5;gbc.fill=GridBagConstraints.BOTH;gbc.weighty=1;
        JLabel empty = new JLabel();
        newPanel.add(empty,gbc);


        return newPanel;
    }

    void Search(){
        String MAIN_QUERY = SQL;

        String strNombre = inputNombre.getText().trim();
        if(!strNombre.isEmpty()){
            if(!strNombre.matches("^[a-zA-Z0-9]+$")){
                JOptionPane.showMessageDialog(this,"EL NOMBRE DEBE SER ALFA-NUMERICO");
                return;
            }   
            
            MAIN_QUERY += " WHERE \"Nombre Completo\" IS LIKE \"" + strNombre + "\"";
        }

        String strCedula = inputCedula.getText().trim();
        if(!strCedula.isEmpty()){
            if(!strCedula.matches("^[a-zA-Z0-9]+$")){
                JOptionPane.showMessageDialog(this,"EL CEDULA DEBE SER ALFA-NUMERICO");
                return;
            }         
            
            if(!strNombre.isEmpty()){MAIN_QUERY += " AND Cedula IS LIKE \"" + strCedula + "\"";}
            else{ MAIN_QUERY += " WHERE Cedula IS LIKE \"" + strCedula + "\"";}
        }

        System.out.println(MAIN_QUERY);

        DATA.setDataVector(new String[][]{}, new String[]{"Nombre Completo", "Cedula", "telefono","Opciones"});
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
}