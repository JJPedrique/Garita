package Frontend.Bitacora;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.DataBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuBitacora extends JPanel {
    
                //region esto va en Menu Inicio pero de momento se queda aqui pq es timido 
                /*
                Object[] parametrosVacios = {};
                String querySesion  = "SET app.usuario_actual = '"+usuario+"'";
                BDD.comandoDML(querySesion,parametrosVacios); // cambiar consultar por otro
                */

    ConexionPostgres DB = new ConexionPostgres();

    String SQL = "SELECT concat(nombre,' ',apellido) AS \"Nombre Completo\", usuario, accion, \n" + "tabla_modificada, fecha_modificacion FROM bitacoras \n" + "LEFT JOIN usuarios on usuarios.cedula = bitacoras.usuario";



    JTextField inputNombre =  new JTextField();
    JTextField inputFechaInicial =  new JTextField();
    JTextField inputFechaFinal =  new JTextField();

    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jspHoraDesde = new JSpinner(new SpinnerDateModel());

    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jspHoraHasta = new JSpinner(new SpinnerDateModel());
    

    //Table
    DefaultTableModel DATA = new DefaultTableModel(new String[][]{}, new String[]{}){
        @Override
        public boolean isCellEditable(int row,int column){return false;}
    };
    public MenuBitacora(){        
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


        gbc.gridy=1;
        JLabel Filtros = new JLabel("Busqueda y Filtros"); 
        Filtros.setForeground(ThemeManager.COLOR_TEXT);       
        Filtros.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.gridy=2;gbc.weightx=0;

        JLabel Nombre = new JLabel("Nombre:");   
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
        JLabel fechaInicial = new JLabel("Fecha Desde:");  
        fechaInicial.setForeground(ThemeManager.COLOR_TEXT);      
        fechaInicial.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(fechaInicial,gbc);

        
        gbc.gridx=1;gbc.weightx=1;
        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(jspHoraDesde, "HH:mm:ss");
        jspHoraDesde.setEditor(editorDesde);
        
        newPanel.add(jdcDesde,gbc);
        gbc.gridx=2;gbc.weightx=1;

        newPanel.add(jspHoraDesde,gbc);

        gbc.gridy=4;gbc.gridx=0;gbc.weightx=0;
    JLabel fechaFinal = new JLabel("Fecha hasta:");  
        fechaFinal.setForeground(ThemeManager.COLOR_TEXT);      
        fechaFinal.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(fechaFinal,gbc);


        gbc.gridx=1;gbc.weightx=1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraHasta, "HH:mm:ss");
        jspHoraHasta.setEditor(editorHasta);
        
        newPanel.add(jdcHasta,gbc);
        gbc.gridx=2;gbc.weightx=1;

        newPanel.add(jspHoraHasta,gbc);




        gbc.gridwidth=2;gbc.gridy=5;gbc.gridx=0;  
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
            
            MAIN_QUERY += " WHERE concat(usuarios.nombre, ' ', usuarios.apellido) ILIKE '" + strNombre + "%'";
        }

        if (jdcDesde.getDate() != null) {
            java.util.Date fecha = jdcDesde.getDate();
            java.util.Date hora = (java.util.Date) jspHoraDesde.getValue();
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fecha);
            
            java.util.Calendar calHora = java.util.Calendar.getInstance();
            calHora.setTime(hora);
            
            cal.set(java.util.Calendar.HOUR_OF_DAY, calHora.get(java.util.Calendar.HOUR_OF_DAY));
            cal.set(java.util.Calendar.MINUTE, calHora.get(java.util.Calendar.MINUTE));
            cal.set(java.util.Calendar.SECOND, calHora.get(java.util.Calendar.SECOND));
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strFechaDesde = sdf.format(cal.getTime());
            
            if (MAIN_QUERY.contains("WHERE")) {
                MAIN_QUERY += " AND bitacoras.fecha_modificacion >= '" + strFechaDesde + "'";
            } else {
                MAIN_QUERY += " WHERE bitacoras.fecha_modificacion >= '" + strFechaDesde + "'";
            }
        }
        

        if (jdcHasta.getDate() != null) {
            java.util.Date fecha = jdcHasta.getDate();
            java.util.Date hora = (java.util.Date) jspHoraHasta.getValue();
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fecha);
            
            java.util.Calendar calHora = java.util.Calendar.getInstance();
            calHora.setTime(hora);
            
            cal.set(java.util.Calendar.HOUR_OF_DAY, calHora.get(java.util.Calendar.HOUR_OF_DAY));
            cal.set(java.util.Calendar.MINUTE, calHora.get(java.util.Calendar.MINUTE));
            cal.set(java.util.Calendar.SECOND, calHora.get(java.util.Calendar.SECOND));
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strFechaHasta = sdf.format(cal.getTime());
            
            if (MAIN_QUERY.contains("WHERE")) {
                MAIN_QUERY += " AND bitacoras.fecha_modificacion <= '" + strFechaHasta + "'";
            } else {
                MAIN_QUERY += " WHERE bitacoras.fecha_modificacion <= '" + strFechaHasta + "'";
            }
        }

        System.out.println(MAIN_QUERY);

        ArrayList<String> headersDB = new ArrayList<>();
        headersDB.add("\"Nombre Completo\"");
        headersDB.add("\"usuario\"");
        headersDB.add("\"accion\"");
        headersDB.add("\"tabla_modificada\"");
        headersDB.add("\"fecha_modificacion\"");
        String[] columnasVisuales = {"Nombre Completo", "Usuario", "Accion", "Tabla Modificada", "Fecha de Modificación"};
        
        try {
            String[][] matrizDatos = GetData(MAIN_QUERY, headersDB);
            DATA.setDataVector(matrizDatos, columnasVisuales);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            DATA.setDataVector(new String[][]{}, columnasVisuales);
        }

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