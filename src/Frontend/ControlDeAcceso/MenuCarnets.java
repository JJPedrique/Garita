package Frontend.ControlDeAcceso;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;

//region JComponentes
class JCarnet {
    JLabel Codigo;
    JLabel NumCasa;
    JLabel Calle;
    JLabel Propietario;
    JButton Borrar;

    public JCarnet(String Codigo, String NumCasa, String Calle, String Propietario, Runnable onRecordDeleted){
        this.Codigo = ThemeManager.Label(Codigo);
        this.NumCasa = ThemeManager.Label(NumCasa);
        this.Calle = ThemeManager.Label(Calle);
        this.Propietario = ThemeManager.Label(Propietario);
        this.Borrar = new JButton(ThemeManager.SetImgIcon("img\\delete.png", ThemeManager.ICON_WIDTH_PX, ThemeManager.ICON_HEIGHT_PX));

        Border margin = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        this.Codigo.setBorder(margin);
        this.NumCasa.setBorder(margin);
        this.Calle.setBorder(margin);
        this.Propietario.setBorder(margin);

        this.Borrar.setFocusPainted(false);
        this.Borrar.setContentAreaFilled(false);
        this.Borrar.setBorderPainted(false);
        this.Borrar.setForeground(ThemeManager.COLOR_TEXT);
        this.Borrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Evento de Borrado
        this.Borrar.addActionListener(e -> {
            Object OPC[] = {"SÍ","NO"};  
            int seleccion = JOptionPane.showOptionDialog(null,
                "¿Seguro que desea eliminar el carnet "+Codigo+"?", 
                "CONFIRMAR ELIMINACIÓN", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE, 
                null, OPC, OPC[1]);  
            
            if (seleccion == JOptionPane.YES_OPTION) {
                try {
                    String Query = "UPDATE carnets SET activo = false WHERE codigo = ?;";
                    Object Parametros[] = {Codigo};
                    ConexionPostgres BD = new ConexionPostgres();
                    BD.comandoDML(Query, Parametros);
                    
                    JOptionPane.showMessageDialog(null, "El carnet ha sido removido del sistema.");

                    // Como JCarnet es una parte aislada del Front, al borrar no puedo actualizar la tabla directamente
                    // El Runnable hace que el evento diaparará una función que desconoce, que podemos decirlo más adelante 
                    // Ver region Tabla -> instanciando el objeto JCarnet (linea 258 aprox)
                    if (onRecordDeleted != null) {
                        onRecordDeleted.run();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al eliminar el carnet: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JPanel toPanel() {
        JPanel ROW = new JPanel(new GridLayout(1,5));
        ROW.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        ROW.setPreferredSize(new Dimension(0, 45));
        ROW.setMinimumSize(new Dimension(0, 45));
        ROW.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        ROW.add(Codigo);
        ROW.add(NumCasa);
        ROW.add(Calle);
        ROW.add(Propietario);
        
        JPanel pBTN = new JPanel(new GridBagLayout());
        pBTN.setOpaque(false);
        pBTN.add(Borrar);
        ROW.add(pBTN);
        
        return ROW;
    }
}
//endregion

public class MenuCarnets extends JPanel {

    //region Componentes
    GridBagLayout GBL = new GridBagLayout();
    GridBagConstraints GBC = new GridBagConstraints();

    private JPanel pFunctions = new JPanel();
    private JPanel pTable = new JPanel();
    private JPanel pTableHeader = new JPanel();
    private JPanel pTableBody = new JPanel();
    
    private JLabel lAgregarCarnet = new JLabel("AGREGAR NUEVO CARNET");
    private JButton bAgregarCarnet = ThemeManager.Button("Agregar");

    private JSeparator hr = new JSeparator();

    private JLabel lBusquedaFiltro = new JLabel("BÚSQUEDA Y FILTROS");
    private JPanel pInputCodigo = new JPanel();
    private JLabel lCodigo = new JLabel("Código");
    private JTextField tfCodigo = ThemeManager.Textfield();
    
    private JButton bBuscar = ThemeManager.Button("Buscar");

    ArrayList<JCarnet> JCarnets = new ArrayList<>();
    String[] headers = {"Código", "Número Casa", "Calle", "Propietario", "Opción"};

    //endregion

    //region Theme
    public void SetTheme(){
        this.setBackground(ThemeManager.COLOR_BACKGROUND);

        pFunctions.setPreferredSize(new Dimension(300, 0));
        pFunctions.setOpaque(false);
        pFunctions.setLayout(GBL);

        lAgregarCarnet.setForeground(ThemeManager.COLOR_TEXT);
        lAgregarCarnet.setFont(ThemeManager.TEXT_SUBTITLE);
        lAgregarCarnet.setHorizontalAlignment(JLabel.CENTER);

        bAgregarCarnet.setPreferredSize(new Dimension(0, 40));
        hr.setForeground(ThemeManager.COLOR_INPUT);
        
        lBusquedaFiltro.setForeground(ThemeManager.COLOR_TEXT);
        lBusquedaFiltro.setFont(ThemeManager.TEXT_SUBTITLE);
        lBusquedaFiltro.setHorizontalAlignment(JLabel.CENTER);
        
        pInputCodigo.setOpaque(false);
        lCodigo.setForeground(ThemeManager.COLOR_TEXT);
        lCodigo.setFont(ThemeManager.TEXT_NORMAL);

        tfCodigo.setBackground(ThemeManager.COLOR_INPUT);
        tfCodigo.setForeground(ThemeManager.COLOR_TEXT);
        tfCodigo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        pTableHeader.setBackground(ThemeManager.COLOR_PRIMARY); 
        pTableHeader.setPreferredSize(new Dimension(0, 40));
        pTableBody.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        pTable.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);

        SetEvents();
    }
    //endregion

    //region Config

    public MenuCarnets(){
        this.setLayout(new BorderLayout(20, 0));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SetTheme();

        GBC.weightx = 1;
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.insets = new Insets(10, 0, 10, 0);
        GBC.gridx = 0; GBC.gridy = 0; pFunctions.add(lAgregarCarnet, GBC);
        
        GBC.gridy = 1; pFunctions.add(bAgregarCarnet, GBC);
        GBC.insets = new Insets(20, 0, 20, 0);
        GBC.gridy = 2; pFunctions.add(hr, GBC);
        GBC.insets = new Insets(10, 0, 10, 0);
        GBC.gridy = 3; pFunctions.add(lBusquedaFiltro, GBC);
        
        pInputCodigo.setLayout(new BorderLayout(10, 0));
        pInputCodigo.add(lCodigo, BorderLayout.WEST);
        pInputCodigo.add(tfCodigo, BorderLayout.CENTER);
        
        GBC.gridy = 4; pFunctions.add(pInputCodigo, GBC);
        
        bBuscar.setPreferredSize(new Dimension(0, 40));
        GBC.insets = new Insets(20, 0, 10, 0);
        GBC.gridy = 5; pFunctions.add(bBuscar, GBC);
        
        GBC.gridy = 6; GBC.weighty = 1.0;
        pFunctions.add(Box.createGlue(), GBC);

        pTable.setLayout(new BorderLayout());
        pTableHeader.setLayout(new GridLayout(1, 5));
        
        for (String h: headers) {
            int alineacion = h.equals("Opción") ? SwingConstants.CENTER : SwingConstants.LEFT;
            JLabel lColumn = new JLabel(h, alineacion);
            lColumn.setForeground(ThemeManager.COLOR_TEXT);
            lColumn.setFont(ThemeManager.TEXT_SUBTITLE);
            
            if (alineacion == SwingConstants.LEFT) lColumn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            pTableHeader.add(lColumn);
        }
        pTable.add(pTableHeader, BorderLayout.NORTH);
        pTableBody.setLayout(GBL);
    
        ActualizarTabla("");
        
        JScrollPane scrollPane = new JScrollPane(pTableBody);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        pTable.add(scrollPane, BorderLayout.CENTER);

        this.add(pFunctions, BorderLayout.WEST);
        this.add(pTable, BorderLayout.CENTER);
    }
    //endregion

    //region Tabla
    public void ActualizarTabla() throws SQLException {
        ActualizarTabla("");
    }

    public void ActualizarTabla(String filtroCodigo) {
        JCarnets.clear();
        pTableBody.removeAll();

        // Query parametrizada para búsquedas parciales seguras
        String Query = "SELECT codigo, numero_vivienda, calle, CONCAT(nombre, ' ', apellido) AS nombre_completo " +
                       "FROM carnets AS C " +
                       "JOIN viviendas AS V ON C.id_vivienda = V.id " + 
                       "JOIN representantes AS R ON R.id_vivienda = V.id " +
                       "WHERE C.activo = true ";

        boolean tieneFiltro = (filtroCodigo != null && !filtroCodigo.trim().isEmpty());
        if (tieneFiltro) {
            Query += "AND C.codigo LIKE ? ";
        }
        Query += "ORDER BY codigo ASC;";

        try {
            ConexionPostgres BDD = new ConexionPostgres();
            Object[] parametros = tieneFiltro ? new Object[]{"%" + filtroCodigo.trim() + "%"} : null;
            ResultSet RS = BDD.consultar(Query, parametros);
            
            while(RS != null && RS.next()){
                String sCodigo = RS.getString("codigo");
                String sNumeroVivienda = RS.getString("numero_vivienda");
                String sCalle = RS.getString("calle");
                String sNombreCompleto = RS.getString("nombre_completo");
                
                JCarnets.add(new JCarnet(sCodigo, sNumeroVivienda, sCalle, sNombreCompleto, () -> {
                    // Aquí definimos que la función que disparará el JCarnet, es el de actualizar la tabla
                    ActualizarTabla(tfCodigo.getText()); 
                }));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        GBC.anchor = GridBagConstraints.NORTH; GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.ipadx = 0; GBC.gridwidth = 1; GBC.weighty = 0;
        
        for(int i=0; i<JCarnets.size(); i++){
            JCarnet actualJCarnets = JCarnets.get(i);
            GBC.gridx = 0; GBC.gridy = i; GBC.weightx = 1; GBC.insets = new Insets((i==0) ? 10 : 5,10,5,10); 
            pTableBody.add(actualJCarnets.toPanel(), GBC);
        }
        
        GBC.anchor = GridBagConstraints.NORTH; 
        GBC.fill = GridBagConstraints.HORIZONTAL;
        GBC.weightx = 1; GBC.weighty = 1;
        GBC.gridx = 0; GBC.gridy = 9999; GBC.gridwidth = 2; 
        pTableBody.add(Box.createGlue(), GBC);
        
        pTableBody.revalidate();
        pTableBody.repaint();
    }
    //endregion

    //region Eventos
    public void SetEvents(){
        bAgregarCarnet.addActionListener(e -> {
            bAgregarCarnet.setEnabled(false);
            bBuscar.setEnabled(false);
            tfCodigo.setEnabled(false);

            JDialog FrameAgregarCarnet = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sistema Garita - Registrar Carnet", true);
            FrameAgregarCarnet.setSize(new Dimension(600, 400));
            FrameAgregarCarnet.setLocationRelativeTo(this);
            FrameAgregarCarnet.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            FrameAgregarCarnet.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    bAgregarCarnet.setEnabled(true);
                    bBuscar.setEnabled(true);
                    tfCodigo.setEnabled(true);
                    
                    ActualizarTabla(tfCodigo.getText());
                }
            });

            FrameAgregarCarnet.add(new FrameAgregarCarnet());
            FrameAgregarCarnet.setVisible(true);
        });

        // Evento para el botón de Búsqueda
        bBuscar.addActionListener(e -> {
            ActualizarTabla(tfCodigo.getText());
        });
    }
    //endregion
}