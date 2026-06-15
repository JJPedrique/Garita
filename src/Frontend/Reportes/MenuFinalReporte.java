package Frontend.Reportes;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

import Backend.*;
import Backend.BDD.DataInputs.*;

public class MenuFinalReporte extends JPanel {
    ConexionPostgres DB = new ConexionPostgres();

//#region SQL QUERY

    String Vivienda = "SELECT viviendas.categoria AS Calle, " +
            "viviendas.numero_vivienda AS Vivienda, " +
            "COUNT(representantes.id) AS  Representantes, " +
            "COUNT(carnets.id) AS Carntes " +
            "FROM viviendas " +
            "JOIN representantes ON viviendas.id = representantes.id_vivienda " +
            "JOIN carnets ON viviendas.id = carnets.id_vivienda " +
            "GROUP BY viviendas.categoria, viviendas.numero_vivienda";

    String Vecinos = "SELECT viviendas.categoria AS Calle, " +
            "viviendas.numero_vivienda AS Vivienda, " +
            "representantes.nombre AS Nombre, " +
            "representantes.apellido AS Apellido, " +
            "representantes.cedula AS Cedula, " +
            "representantes.telefono AS Telefono " +
            "FROM representantes " +
            "JOIN viviendas ON viviendas.id = representantes.id_vivienda ";

    String Carnets = "SELECT viviendas.categoria AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " +
                "carnets.codigo AS Codigo " + 
                "FROM carnets " +
                "JOIN viviendas ON viviendas.id = carnets.id_vivienda"; 

    String RegistrosAcceso = "SELECT accesos.fecha_hora AS \"Fecha de Acceso\"," + 
                " accesos.tipo AS Tipo, " + 
                " accesos.estado AS Estado, " + 
                " viviendas.categoria AS Calle, " + 
                " viviendas.numero_vivienda AS Vivienda, " + 
                " carnets.codigo AS Codigo, " + 
                " accesos.nombre_visita AS Visita " + 
                "FROM accesos " + 
                "LEFT JOIN carnets ON carnets.id = accesos.id_carnet " + 
                "LEFT JOIN viviendas ON viviendas.id = carnets.id_vivienda";

    String Cuotas = "SELECT cuotas.descripcion AS Descripcion, " + 
                "cuotas.fecha_emision AS \"Fecha de Emision\", " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.fecha_limite AS \"Fecha Limite de pago\", " + 
                "COUNT(pagos_realizados.id) AS \"Cuotas Pagadas\", " + 
                "(SELECT COUNT(*) FROM viviendas) - COUNT(pagos_realizados.id) AS \"Cuotas No Pagadas\" " + 
                "FROM cuotas " + 
                "LEFT JOIN pagos_realizados ON cuotas.id = pagos_realizados.id_cuota " + 
                "GROUP BY descripcion,fecha_emision, monto, fecha_limite";

    String PagosRealizado = "SELECT viviendas.categoria AS Calle, " + 
                "viviendas.numero_vivienda AS Vivienda, " + 
                "cuotas.monto AS Monto, " + 
                "cuotas.descripcion AS Descripcion, " + 
                "pagos_realizados.tipo_pago AS \"Tipo de Pago\", " + 
                "pagos_realizados.referencia AS Referencia, " + 
                "pagos_realizados.fecha_de_pago AS \"Fecha de Pago\" " + 
                "FROM pagos_realizados " + 
                "JOIN cuotas ON cuotas.id = pagos_realizados.id_cuota " + 
                "JOIN viviendas ON viviendas.id = pagos_realizados.id_vivienda";

    String Bitacora = "SELECT bitacoras.usuario AS Usuario, " + 
                "bitacoras.accion AS Accion, " + 
                "bitacoras.tabla_modificada AS \"Tabla Modificada\", " + 
                "bitacoras.fecha_modificacion AS \"Fecha de Modificacion\" " + 
                "FROM bitacoras";

//#endregion
    
//#region FRONTEND

    JPanel CheckPanel = new JPanel(new GridBagLayout()); //SELECT
    JPanel FilterPanel = new JPanel(new GridBagLayout()); //WHERE
    
    JComboBox<String> MenuColumnas;
    JComboBox<String> OrdernarColumnasMenu,OrdernarMenu;
    JSpinner LimitInput;




    Map<String,String> TableHeader = new HashMap<>();
    ArrayList<JCheckBox> isVisible = new ArrayList<>();
    ArrayList<JPanel> Filters = new ArrayList<>();

    public MenuFinalReporte() throws SQLException{
        this.setLayout(new BorderLayout());
        this.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        JTabbedPane Menu = new JTabbedPane();
        Menu.addTab("Modulo",Vista());
        Menu.addTab( "Filtros",Filtros());
        Menu.addTab("Imprimir",Imprimir());

        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,Menu,Preview()),BorderLayout.CENTER);

        //UPDATES
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "executeSearch");
        this.getActionMap().put("executeSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        ChangeModule("Viviendas");
    }

    JPanel Preview(){
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(new JScrollPane(new JTable()),BorderLayout.CENTER);
        return newPanel;
    } 

    JPanel Vista(){
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
        String OpcionesModulos[] = {"Viviendas","Representantes","Carnets","Registros de Acceso","Cuotas","Pagos Realizados","Bitacora"};
        JComboBox MenuModulos = new JComboBox<>(OpcionesModulos);
        newPanel.add(MenuModulos,gbc);
        MenuModulos.setFont(ThemeManager.TEXT_NORMAL);
        MenuModulos.setForeground(ThemeManager.COLOR_TEXT_DARK);
        MenuModulos.setBackground(ThemeManager.COLOR_SECONDARY);
        MenuModulos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ChangeModule(MenuModulos.getSelectedItem().toString());
                } catch (SQLException e1) {e1.printStackTrace();}
            }
        });


        gbc.gridx=0; gbc.gridy=1;
        gbc.gridwidth=2; gbc.weightx=1;gbc.weighty=1;
        CheckPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        newPanel.add(new JScrollPane(CheckPanel),gbc);

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
        MenuColumnas = new JComboBox<String>();
        newPanel.add(MenuColumnas,gbc);
        MenuColumnas.setFont(ThemeManager.TEXT_NORMAL);
        MenuColumnas.setForeground(ThemeManager.COLOR_TEXT_DARK);
        MenuColumnas.setBackground(ThemeManager.COLOR_SECONDARY);

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
                AddCondition(MenuColumnas.getSelectedItem().toString());
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
        FilterPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        newPanel.add(new JScrollPane(FilterPanel),gbc);

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
        OrdernarColumnasMenu = new JComboBox<>();
        newPanel.add(OrdernarColumnasMenu,gbc);

        gbc.gridx=2;     
        String OrderBy[] = {"Ascendente","Descendente"};
        OrdernarMenu = new JComboBox<>(OrderBy);
        newPanel.add(OrdernarMenu,gbc);

        gbc.gridy=1; gbc.gridx=0;
        JLabel limite = new JLabel("Limite:");
        limite.setFont(ThemeManager.TEXT_NORMAL);
        limite.setForeground(ThemeManager.COLOR_TEXT);
        newPanel.add(limite,gbc);    
        
        gbc.gridx=1; 
        LimitInput = new JSpinner( new SpinnerNumberModel(0, 0, 1000, 1));
        newPanel.add(LimitInput,gbc);

        gbc.gridx=2; 
        LimitInput = new JSpinner( new SpinnerNumberModel(100, 0, 1000, 1));
        newPanel.add(LimitInput,gbc);

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
        for (Component C : CheckPanel.getComponents()) {CheckPanel.remove(C);}
        for (Component C : FilterPanel.getComponents()) {FilterPanel.remove(C);}
        TableHeader.clear();
        isVisible.clear();
        Filters.clear();

        String SQL_query = "";
        switch (Module) {
            case "Viviendas": SQL_query = Vivienda ; break;
            case "Representantes": SQL_query = Vecinos; break;
            case "Carnets": SQL_query = Carnets; break;
            case "Registros de Acceso": SQL_query = RegistrosAcceso; break;
            case "Cuotas": SQL_query = Cuotas; break;
            case "Pagos Realizados": SQL_query = PagosRealizado; break;
            case "Bitacora": SQL_query = Bitacora; break;
        }

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
            CheckPanel.add(newCheck,gbc);
            gbc.gridy++;

            newCheck.setSelected(true);
            newCheck.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
            newCheck.setForeground(ThemeManager.COLOR_TEXT);
            newCheck.setFont(ThemeManager.TEXT_NORMAL);
        }

        gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        CheckPanel.add(new JLabel(),gbc);

        //Filtros
        MenuColumnas.removeAllItems();
        OrdernarColumnasMenu.removeAllItems();
        for(String k : TableHeader.keySet()){
            MenuColumnas.addItem(k);
            OrdernarColumnasMenu.addItem(k);
        }   
 
        this.revalidate();
        this.repaint();
    }

    void AddCondition(String Column){
        if(FilterPanel.getComponentCount() != 0){FilterPanel.remove(FilterPanel.getComponentCount()-1);}

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
                Filters.remove(newCondition);
                FilterPanel.remove(newCondition);
                FilterPanel.revalidate();
                FilterPanel.repaint();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0; gbc.gridy = Filters.size();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        FilterPanel.add(newCondition,gbc);
        Filters.add(newCondition);
        
        gbc.gridy++;
        gbc.weighty=1;gbc.fill= GridBagConstraints.BOTH;
        FilterPanel.add(new JLabel(),gbc);
        this.revalidate();
        this.repaint();
    }

//#endregion

//#region EXPORTAR PDF
    void ImprimirPDF(){
        System.out.println("Proximamente...");
    }
//#endregion
}
