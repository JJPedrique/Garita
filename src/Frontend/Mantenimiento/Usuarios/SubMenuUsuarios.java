package Frontend.Mantenimiento.Usuarios;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class SubMenuUsuarios extends JPanel {

    class MyTable extends JPanel {
        class MyRow extends JPanel{
            ArrayList<String> DATA;
            public MyRow(ArrayList<String> Data){
                DATA = Data;
                this.setLayout(new GridBagLayout());
                this.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
                GridBagConstraints gbc = new GridBagConstraints(); 
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx=1;gbc.gridx=0;gbc.gridy=0;
                gbc.insets = new Insets(5,5,5,5);
                for(int i = 0; i<DATA.size(); i++){
                    if(i==0){continue;}
                    this.add(ThemeManager.Label(DATA.get(i)),gbc);gbc.gridx+=1;
                }

                gbc.weightx=0;
                JPanel PanelControl = ThemeManager.Panel(new GridBagLayout());
                this.add(PanelControl,gbc);

                gbc.gridx=0;
                JButton Editar = new JButton(ThemeManager.SetImgIcon("img\\edit.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
                PanelControl.add(Editar,gbc);
                Editar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new FrameModificarUsuario(Integer.parseInt(DATA.get(0)));
                         try {Search();} catch (SQLException e1) {e1.printStackTrace();}
                    }
                });
                
                Editar.setFocusPainted(false);
                Editar.setContentAreaFilled(false);
                Editar.setBorderPainted(false);
                Editar.setForeground(ThemeManager.COLOR_TEXT);
                Editar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                
                gbc.gridx=1;
                JButton Eliminar = new JButton(ThemeManager.SetImgIcon("img\\delete.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
                PanelControl.add(Eliminar,gbc);
                Eliminar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new FrameBorrarUsuario(Integer.parseInt(DATA.get(0)));
                         try {Search();} catch (SQLException e1) {e1.printStackTrace();}
                    }
                });

                Eliminar.setFocusPainted(false);
                Eliminar.setContentAreaFilled(false);
                Eliminar.setBorderPainted(false);
                Eliminar.setForeground(ThemeManager.COLOR_TEXT);
                Eliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        ArrayList<MyRow> ROWS = new ArrayList<>();
        JPanel RowsPanel = ThemeManager.Panel(new GridBagLayout());

        public MyTable(String[] Headers){
            this.setLayout(new BorderLayout());
            this.add(HeadersPanel(Headers),BorderLayout.NORTH);
            this.add(new JScrollPane(RowsPanel),BorderLayout.CENTER);   
            RowsPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        }

        JPanel HeadersPanel(String[] Headers){
            JPanel newPanel = new JPanel(new GridBagLayout());
            newPanel.setBackground(ThemeManager.COLOR_PRIMARY);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx=1;gbc.gridx=0;gbc.gridy=0;
            gbc.insets = new Insets(10,10,10,10);
            
            for(int i = 0;i<Headers.length;i++){
                if(i==0){continue;}
                JLabel newLabel = ThemeManager.Label(Headers[i]);
                newLabel.setFont(ThemeManager.TEXT_SUBTITLE);
                newPanel.add(newLabel,gbc);gbc.gridx+=1;
            }

            gbc.weightx=0;
            JLabel newLabel = ThemeManager.Label("Opciones");
            newLabel.setFont(ThemeManager.TEXT_SUBTITLE);
            newPanel.add(newLabel,gbc);
            return newPanel;
        }

        void UpdateTable(ArrayList<ArrayList<String>> newRows){
            for (Component C : RowsPanel.getComponents()) {RowsPanel.remove(C);}
            ROWS.clear();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx=1;gbc.gridx=0;gbc.gridy=0;
            gbc.insets = new Insets(10,10,5,10);
            
            for (ArrayList<String> R: newRows) {
                MyRow newRow = new MyRow(R);
                RowsPanel.add(newRow,gbc);
                ROWS.add(newRow);
                gbc.gridy+=1;
            }

            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty=1;
            RowsPanel.add(new JLabel(""),gbc);

            this.repaint();
            this.revalidate();
        }
    }

    ConexionPostgres DB = new ConexionPostgres();

    String Headers[] = {"id","Nombre", "Apellido", "Rol", "Cedula", "Telefono"};
    String SQL = "SELECT id,nombre,apellido,rol, Cedula, telefono FROM usuarios";

    JTextField inputNombre =  ThemeManager.Textfield();
    JTextField inputCedula =  ThemeManager.Textfield();
    JTextField inputApellido = ThemeManager.Textfield();

    //Table
    MyTable UserTable;

    public SubMenuUsuarios(){
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        UserTable = new MyTable(Headers);
        this.add(Filtros(),BorderLayout.WEST);
        this.add(UserTable,BorderLayout.CENTER);
        try {Search();} catch (SQLException e1) {e1.printStackTrace();}

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {Search();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });
    }

    JPanel Filtros(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridwidth=2;

        JButton AddUser = ThemeManager.Button("Agregar Nuevo Usuario");
        AddUser.setFont(ThemeManager.TEXT_SUBTITLE);

        newPanel.add(AddUser,gbc);
        AddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FrameAgregarUsuario();
                try {Search();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridy=1;
        JLabel Filtros = ThemeManager.Label("Busqueda y Filtros"); 
        Filtros.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.weightx=0;
        
        gbc.gridy=2;gbc.gridx=0;
        JLabel Nombre = ThemeManager.Label("Nombre");   
        newPanel.add(Nombre,gbc);
     
        gbc.gridy=3;
        JLabel Apellido = ThemeManager.Label("Apellido");   
        newPanel.add(Apellido,gbc);        
        
        gbc.gridy=4;
        JLabel Cedula = ThemeManager.Label("Cedula");  
        newPanel.add(Cedula,gbc);

        gbc.gridy=2;gbc.gridx=1;gbc.weightx=1;
        inputNombre =  ThemeManager.Textfield();
        newPanel.add(inputNombre,gbc);

        gbc.gridy=3;
        inputApellido =  ThemeManager.Textfield();
        newPanel.add(inputApellido,gbc);
    
        gbc.gridy=4;
        inputCedula =  ThemeManager.Textfield();
        newPanel.add(inputCedula,gbc);

        gbc.gridwidth=2;gbc.gridy=5;gbc.gridx=0;  
        JButton Buscar = ThemeManager.Button("Buscar");
        newPanel.add(Buscar,gbc);
        Buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Buscar");
                try {Search();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridy=6;gbc.fill=GridBagConstraints.BOTH;gbc.weighty=1;
        JLabel empty = new JLabel();
        newPanel.add(empty,gbc);
        return newPanel;
    }

    void Search() throws SQLException{
        ArrayList<Object> PARAM = new ArrayList<>();
        PARAM.add(true);
        ArrayList<String> COND = new ArrayList<>();

        String strNombre = inputNombre.getText().trim();
        String strApellido = inputApellido.getText().trim();
        String strCedula = inputCedula.getText().trim();
        
        if(!strNombre.isEmpty()){
            if(!strNombre.matches("^[a-zA-Z0-9]+$")){
                ThemeManager.MostrarMensajeError(this,"EL CEDULA DEBE SER ALFA-NUMERICO");
                return;
            }    

            PARAM.add("%"+strNombre+"%");
            COND.add("nombre ILIKE  ?");
        }

        if(!strApellido.isEmpty()){
            if(!strApellido.matches("^[a-zA-Z0-9]+$")){
                ThemeManager.MostrarMensajeError(this,"EL CEDULA DEBE SER ALFA-NUMERICO");
                return;
            }    

            PARAM.add("%"+strApellido+"%");
            COND.add("apellido ILIKE  ?");
        }

        if(!strCedula.isEmpty()){
            if(!strCedula.matches("^[a-zA-Z0-9]+$")){
                ThemeManager.MostrarMensajeError(this,"EL CEDULA DEBE SER ALFA-NUMERICO");
                return;
            }    

            PARAM.add("%"+strCedula+"%");
            COND.add("cedula ILIKE  ?");
        }

        String WHERE = PARAM.size() > 1 ? "WHERE activo = ? AND " + String.join(" AND ", COND) :  "WHERE activo = ? ";
        String ORDER_BY = "ORDER BY rol ASC";
        String MAIN_QUERY = String.join(" ",new String[]{SQL,WHERE,ORDER_BY});


        ArrayList<ArrayList<String>> Datas = new ArrayList<>();
        ResultSet RS_DATA = DB.consultar(MAIN_QUERY,PARAM.toArray());

        while (RS_DATA.next()) {            
            ArrayList<String> newData = new ArrayList<>();
            for(String h: Headers){newData.add(RS_DATA.getString(h));}
            Datas.add(newData);
        }     
        

        UserTable.UpdateTable(Datas);

        this.repaint();
        this.revalidate();
    }

}