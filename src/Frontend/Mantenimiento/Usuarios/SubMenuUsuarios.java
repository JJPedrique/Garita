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
        String[] headers;
        JPanel TablePanel = ThemeManager.Panel(new GridBagLayout());

        public MyTable(String[] Headers){
            this.headers = Headers;
            this.setLayout(new BorderLayout());        
            this.setBackground(ThemeManager.COLOR_BACKGROUND);
            this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            TablePanel.setBackground(ThemeManager.COLOR_TEXT_DARK);
            // Agregamos el panel unificado (cabeceras + filas) al ScrollPane
            
            JScrollPane newScroll = new JScrollPane(TablePanel);
            this.add(newScroll, BorderLayout.CENTER);  
            newScroll.setBorder(BorderFactory.createEmptyBorder());
            newScroll.setViewportBorder(null);            
        }

        void UpdateTable(ArrayList<ArrayList<String>> newRows){
            // Limpiamos el panel principal
            TablePanel.removeAll();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // ---------------------------------------------------------
            // 1. DIBUJAR CABECERAS
            // ---------------------------------------------------------
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.ipady=20;gbc.ipadx=20;

            for(int i = 1; i < headers.length; i++){

                JLabel newLabel = ThemeManager.Label(headers[i]);
                newLabel.setHorizontalAlignment(SwingConstants.CENTER);
                newLabel.setFont(ThemeManager.TEXT_SUBTITLE);
                newLabel.setOpaque(true);
                newLabel.setBackground(ThemeManager.COLOR_PRIMARY);
                TablePanel.add(newLabel, gbc);
                gbc.gridx++;
            }

            // Cabecera de la columna de Botones
            gbc.weightx = 0; // Las opciones no necesitan expandirse tanto como el texto
            JLabel opcionesLabel = ThemeManager.Label("Opciones");
            opcionesLabel.setFont(ThemeManager.TEXT_SUBTITLE);
            opcionesLabel.setOpaque(true);
            opcionesLabel.setBackground(ThemeManager.COLOR_PRIMARY);
            TablePanel.add(opcionesLabel, gbc);

            // ---------------------------------------------------------
            // 2. DIBUJAR FILAS DE DATOS
            // ---------------------------------------------------------
            gbc.gridy = 1;
            
            
            for (ArrayList<String> R : newRows) {
                gbc.gridx = 0;
                gbc.weightx = 1;
                gbc.ipady=20;gbc.ipadx=20;

                // Obtenemos el ID de la fila (asumiendo que está en la posición 0) para pasarlo a los botones
                final int rowId = (R.size() > 0 && R.get(0) != null) ? Integer.parseInt(R.get(0)) : -1;

                // Iteramos sobre la longitud de las cabeceras para rellenar espacios vacíos
                for(int i = 0; i < headers.length; i++){
                    if(i == 0) { continue; } 
                    gbc.insets=new Insets(10,0,10,0);
                    if(i==1){gbc.insets=new Insets(10,10,10,0);}   

                    String cellText = " "; // Por defecto un espacio para que el layout no colapse
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

                // ---------------------------------------------------------
                // 3. DIBUJAR BOTONES DE OPCIONES
                // ---------------------------------------------------------
                gbc.weightx = 0;
                gbc.insets = new Insets(10, 0, 10, 10);
                gbc.ipady=2;gbc.ipadx=2;

                JPanel PanelControl = ThemeManager.Panel(new GridBagLayout());
                PanelControl.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
                
                GridBagConstraints btnGbc = new GridBagConstraints();
                btnGbc.gridy = 0;
                
                // Botón Editar
                btnGbc.gridx = 0;
                JButton Editar = new JButton(ThemeManager.SetImgIcon("img\\edit.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
                EstilizarBoton(Editar);
                Editar.addActionListener(e -> {
                    new FrameModificarUsuario(rowId);
                    try { Search(); } catch (SQLException e1) { e1.printStackTrace(); }
                });
                PanelControl.add(Editar, btnGbc);
                
                // Botón Eliminar
                btnGbc.gridx = 1;
                JButton Eliminar = new JButton(ThemeManager.SetImgIcon("img\\delete.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));
                EstilizarBoton(Eliminar);
                Eliminar.addActionListener(e -> {
                    new FrameBorrarUsuario(rowId);
                    try { Search(); } catch (SQLException e1) { e1.printStackTrace(); }
                });
                PanelControl.add(Eliminar, btnGbc);

                // Añadimos el panel de botones al final de la fila en el panel principal
                TablePanel.add(PanelControl, gbc);
                gbc.gridy++;
            }

            // ---------------------------------------------------------
            // 4. ESPACIO FINAL PARA EMPUJAR HACIA ARRIBA
            // ---------------------------------------------------------
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;gbc.gridx=1;gbc.gridwidth=headers.length;
            TablePanel.add(new JLabel(""), gbc);

            this.repaint();
            this.revalidate();
        }

        // Método auxiliar para no repetir las líneas de estilo en cada botón
        private void EstilizarBoton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setForeground(ThemeManager.COLOR_TEXT);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    String[] Headers = {"id","Nombre","Apellido", "Rol", "Cédula", "Teléfono"};
    String SQL = "SELECT id,nombre AS \"Nombre\",apellido AS \"Apellido\",rol AS \"Rol\", Cedula AS \"Cédula\", telefono AS \"Teléfono\" FROM usuarios";

    JTextField inputNombre =  ThemeManager.Textfield("Ej: Carlos");
    JTextField inputApellido = ThemeManager.Textfield("Ej: Mendoza");
    JTextField inputCedula =  ThemeManager.Textfield("Ej: V-12345678");
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
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;gbc.weightx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridwidth=2;

        JLabel AddUser = ThemeManager.Label("AGREGAR NUEVO USUARIO");
        AddUser.setFont(ThemeManager.TEXT_SUBTITLE);
        AddUser.setHorizontalAlignment(SwingConstants.CENTER);
        newPanel.add(AddUser,gbc);

        gbc.gridy=1;
        JButton BtnAddUser = ThemeManager.Button("Agregar Nuevo Usuario");
        BtnAddUser.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(BtnAddUser,gbc);
        BtnAddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FrameAgregarUsuario();
                try {Search();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridy=2;
        JSeparator hr = new JSeparator(JSeparator.HORIZONTAL);
        newPanel.add(hr,gbc);

        gbc.gridy=3;
        JLabel Filtros = ThemeManager.Label("BÚSQUEDA Y FILTROS"); 
        Filtros.setFont(ThemeManager.TEXT_SUBTITLE);
        Filtros.setHorizontalAlignment(SwingConstants.CENTER);
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.weightx=0;
        
        gbc.gridy=4;gbc.gridx=0;
        JLabel Nombre = ThemeManager.Label("Nombre");   
        newPanel.add(Nombre,gbc);
     
        gbc.gridy=5;
        JLabel Apellido = ThemeManager.Label("Apellido");   
        newPanel.add(Apellido,gbc);        
        
        gbc.gridy=6;
        JLabel Cedula = ThemeManager.Label("Cédula");  
        newPanel.add(Cedula,gbc);

        gbc.gridy=4;gbc.gridx=1;gbc.weightx=1;
        newPanel.add(inputNombre,gbc);

        gbc.gridy=5;
        newPanel.add(inputApellido,gbc);
    
        gbc.gridy=6;
        newPanel.add(inputCedula,gbc);

        gbc.gridwidth=2;gbc.gridy=7;gbc.gridx=0;  
        JButton Buscar = ThemeManager.Button("Buscar");
        newPanel.add(Buscar,gbc);
        Buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Buscar");
                try {Search();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridy=8;gbc.fill=GridBagConstraints.BOTH;gbc.weighty=1;
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
            //if(!strNombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")){
            //    ThemeManager.MostrarMensajeError(this,"El campo \"Nombre\" solo debe Contener letras y espacios.");
            //    return;
            //}    

            PARAM.add("%"+strNombre+"%");
            COND.add("nombre ILIKE  ?");
        }

        if(!strApellido.isEmpty()){
            //if(!strApellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")){
            //    ThemeManager.MostrarMensajeError(this,"El campo \"Apellido\" solo debe Contener letras y espacios.");
            //    return;
            //}    

            PARAM.add("%"+strApellido+"%");
            COND.add("apellido ILIKE  ?");
        }

        if(!strCedula.isEmpty()){
            //if(!strCedula.matches("^[VEve][-]\\d{7,8}$")){
            //    ThemeManager.MostrarMensajeError(this,"El campo \"Cédula\" no es valido.");
            //    return;
            //}    

            PARAM.add("%"+strCedula+"%");
            COND.add("cedula ILIKE  ?");
        }

        String WHERE = PARAM.size() > 1 ? "WHERE activo = ? AND " + String.join(" AND ", COND) :  "WHERE activo = ? ";
        String ORDER_BY = "ORDER BY rol ASC";
        String MAIN_QUERY = String.join(" ",new String[]{SQL,WHERE,ORDER_BY});


        ArrayList<ArrayList<String>> Datas = new ArrayList<>();
        ResultSet RS_DATA = ConexionPostgres.consultar(MAIN_QUERY,PARAM.toArray());

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