package Frontend.Cuotas;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Backend.ThemeManager;

import java.awt.*;
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

public class MenuCuotas extends JPanel {

    ConexionPostgres DB = new ConexionPostgres();

    String SQL = "SELECT descripcion, monto, fecha_emision, fecha_limite, activo FROM cuotas";


    JTextField inputDescripcion =  new JTextField();
    JTextField inputMonto = new JTextField();
    JTextField inputFechaInicial =  new JTextField();
    JTextField inputFechaFinal =  new JTextField();
    
        JRadioButton radioTodos = new JRadioButton("Todos");
        JRadioButton radioActivo = new JRadioButton("Activo");
        JRadioButton radioInactivo = new JRadioButton("Inactivo");


        


    private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jspHoraDesde = new JSpinner(new SpinnerDateModel());

    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jspHoraHasta = new JSpinner(new SpinnerDateModel());
    
    DefaultTableModel DATA = new DefaultTableModel(new String[][]{}, new String[]{}){
@Override
public boolean isCellEditable(int row,int column){return false;}
};

    public MenuCuotas(){        
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
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridwidth=2;


        JButton btnAddCuota = new JButton("Programar Cuota");
        btnAddCuota.setFont(ThemeManager.TEXT_NORMAL);
        btnAddCuota.setForeground(ThemeManager.COLOR_TEXT);
        btnAddCuota.setBackground(ThemeManager.COLOR_PRIMARY);
        btnAddCuota.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(btnAddCuota,gbc);

        btnAddCuota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ventanaPadre = SwingUtilities.getWindowAncestor(MenuCuotas.this);
                if (ventanaPadre instanceof JFrame) {
                    VentanaProgramarCuota dialog = new VentanaProgramarCuota((JFrame) ventanaPadre, MenuCuotas.this);
                    dialog.setVisible(true);
            }
        }
        });


        gbc.gridy=1;
        JLabel Filtros = new JLabel("Busqueda y Filtros"); 
        Filtros.setForeground(ThemeManager.COLOR_TEXT);       
        Filtros.setFont(ThemeManager.TEXT_SUBTITLE);
        newPanel.add(Filtros,gbc);

        gbc.gridwidth=1;gbc.gridy=2;gbc.weightx=0;

        JLabel Descripcion = new JLabel("Descripcion:");   
        Descripcion.setForeground(ThemeManager.COLOR_TEXT);     
        Descripcion.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(Descripcion,gbc);
        

        gbc.gridx=1;gbc.weightx=1;
        inputDescripcion =  new JTextField();
        inputDescripcion.setFont(ThemeManager.TEXT_NORMAL);
        inputDescripcion.setForeground(ThemeManager.COLOR_TEXT);
        inputDescripcion.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(inputDescripcion,gbc);

        gbc.gridwidth=1;gbc.gridy=3;gbc.weightx=0;gbc.gridx=0;

        JLabel Monto = new JLabel("Monto ($):");   
        Monto.setForeground(ThemeManager.COLOR_TEXT);     
        Monto.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(Monto,gbc);
        

        gbc.gridx=1;gbc.weightx=1;
        inputMonto =  new JTextField();
        inputMonto.setFont(ThemeManager.TEXT_NORMAL);
        inputMonto.setForeground(ThemeManager.COLOR_TEXT);
        inputMonto.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(inputMonto,gbc);



        
        ButtonGroup grupo= new ButtonGroup();   
        grupo.add(radioTodos);
        grupo.add(radioActivo);
        grupo.add(radioInactivo);
        
        gbc.gridy=4;gbc.gridx=0;gbc.weightx=0;
        radioTodos.setFont(ThemeManager.TEXT_NORMAL);
        radioTodos.setForeground(ThemeManager.COLOR_TEXT);
        radioTodos.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(radioTodos,gbc);

        gbc.gridy=4;gbc.gridx=1;gbc.weightx=0;
        radioActivo.setFont(ThemeManager.TEXT_NORMAL);
        radioActivo.setForeground(ThemeManager.COLOR_TEXT);
        radioActivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(radioActivo,gbc);

        
        gbc.gridy=4;gbc.gridx=2;gbc.weightx=0;
        radioInactivo.setFont(ThemeManager.TEXT_NORMAL);
        radioInactivo.setForeground(ThemeManager.COLOR_TEXT);
        radioInactivo.setBackground(ThemeManager.COLOR_BACKGROUND);
        newPanel.add(radioInactivo,gbc);


        gbc.gridy=5;gbc.gridx=0;gbc.weightx=0;
        JLabel fechaInicial = new JLabel("Fecha Emisión:");  
        fechaInicial.setForeground(ThemeManager.COLOR_TEXT);      
        fechaInicial.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(fechaInicial,gbc);

        
        gbc.gridx=1;gbc.weightx=1;
        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(jspHoraDesde, "HH:mm:ss");
        jspHoraDesde.setEditor(editorDesde);
        
        newPanel.add(jdcDesde,gbc);
        gbc.gridx=2;gbc.weightx=1;

        newPanel.add(jspHoraDesde,gbc);

        gbc.gridy=6;gbc.gridx=0;gbc.weightx=0;
    JLabel fechaFinal = new JLabel("Fecha limite:");  
        fechaFinal.setForeground(ThemeManager.COLOR_TEXT);      
        fechaFinal.setFont(ThemeManager.TEXT_NORMAL);
        newPanel.add(fechaFinal,gbc);


        gbc.gridx=1;gbc.weightx=1;
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraHasta, "HH:mm:ss");
        jspHoraHasta.setEditor(editorHasta);
        
        newPanel.add(jdcHasta,gbc);
        gbc.gridx=2;gbc.weightx=1;

        newPanel.add(jspHoraHasta,gbc);




        gbc.gridwidth=2;gbc.gridy=7;gbc.gridx=0;  
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

        String strDesc = inputDescripcion.getText().trim();
        if(!strDesc.isEmpty()){
            if(!strDesc.matches("^[a-zA-Z0-9ñÑ ]+$")){
                JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN DEBE SER ALFA-NUMERICO");
                return;
            }   
            
            MAIN_QUERY += " WHERE descripcion ILIKE'" + strDesc + "%'";
        }
        String strMonto = inputMonto.getText().trim();
        if(!strMonto.isEmpty()){
            if(!strMonto.matches("^[0-9]+(\\.[0-9]{1,2})?$")){
                JOptionPane.showMessageDialog(this,"EL MONTO DEBE SER NUMÉRICO (EJ: 10 o 12.50)");
                return;
            }   
            
            if (MAIN_QUERY.contains("WHERE")) {
                
                MAIN_QUERY += " AND monto = " + strMonto ;
            }else{
                MAIN_QUERY += " WHERE monto = " + strMonto ;

            }

        }
        
        if (radioActivo.isSelected()) {
            if (MAIN_QUERY.contains("WHERE")) {
                MAIN_QUERY += " AND activo = true";
            } else {
                MAIN_QUERY += " WHERE activo = true";
            }
        } else if (radioInactivo.isSelected()) {
            if (MAIN_QUERY.contains("WHERE")) {
                MAIN_QUERY += " AND activo = false";
            } else {
                MAIN_QUERY += " WHERE activo = false";
            }
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
                MAIN_QUERY += " AND fecha_emision >= '" + strFechaDesde + "'";
            } else {
                MAIN_QUERY += " WHERE fecha_emision >= '" + strFechaDesde + "'";
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
                MAIN_QUERY += " AND fecha_limite <= '" + strFechaHasta + "'";
            } else {
                MAIN_QUERY += " WHERE fecha_limite <= '" + strFechaHasta + "'";
            }
        }

        System.out.println(MAIN_QUERY);

        ArrayList<String> headersDB = new ArrayList<>();
        headersDB.add("\"descripcion\"");
        headersDB.add("\"monto\"");
        headersDB.add("\"fecha_emision\"");
        headersDB.add("\"fecha_limite\"");
        headersDB.add("\"activo\"");
        String[] columnasVisuales = {"Descripción", "Monto", "Fecha Emisión", "Fecha Limite", "Activo", "Opciones"};
        
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