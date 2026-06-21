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
        JPanel newPanel = ThemeManager.Panel(new BorderLayout());
        newPanel.add(ThemeManager.ScrollPanel(ThemeManager.Table(DATA)),BorderLayout.CENTER);
        return newPanel;
    } 


    JPanel Filtros(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth=2;

        JButton AddUser = ThemeManager.Button("Agregar Nuevo Usuario");
        newPanel.add(AddUser,gbc);
        AddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FrameAgregarUsuario();
            }
        });

        gbc.gridy=1;
        JLabel Filtros = ThemeManager.Label("Busqueda y Filtros"); 
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.gridy=2;gbc.weightx=0;
        JLabel Nombre = ThemeManager.Label("Nombre");   
        newPanel.add(Nombre,gbc);
        
        gbc.gridx=1;gbc.weightx=1;
        inputNombre =  ThemeManager.Textfield();
        newPanel.add(inputNombre,gbc);

        gbc.gridy=3;gbc.gridx=0;gbc.weightx=0;
        JLabel Cedula = ThemeManager.Label("Cedula");  
        newPanel.add(Cedula,gbc);

        gbc.gridx=1;gbc.weightx=1;
        inputCedula =  ThemeManager.Textfield();
        newPanel.add(inputCedula,gbc);

        gbc.gridwidth=2;gbc.gridy=4;gbc.gridx=0;  
        JButton Buscar = ThemeManager.Button("Buscar");
        newPanel.add(Buscar,gbc);
        Buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Buscar");
                Search();
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

        //System.out.println(MAIN_QUERY);

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