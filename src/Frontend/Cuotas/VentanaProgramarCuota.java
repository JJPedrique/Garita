package Frontend.Cuotas;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Backend.ConexionPostgres;
import Backend.ThemeManager;

public class VentanaProgramarCuota extends JDialog {
    private JTextField inputDescription;
    private JTextField txtMonto;
    private final MenuCuotas menuPadre; 
    ConexionPostgres DB = new ConexionPostgres();

    String queryInsert = "INSERT INTO cuotas (descripcion, monto, fecha_emision, fecha_limite, activo) VALUES (?, ?, ?, ?, ?)";
    
        private final JDateChooser jdcDesde = new JDateChooser();
    private final JSpinner jspHoraDesde = new JSpinner(new SpinnerDateModel());

    private final JDateChooser jdcHasta = new JDateChooser();
    private final JSpinner jspHoraHasta = new JSpinner(new SpinnerDateModel());
    

    public VentanaProgramarCuota(JFrame framePadre, MenuCuotas menuPadre) {
        super(framePadre, "Sistema Garita - Agregar Cuota", true); 
        this.menuPadre = menuPadre;
        
        setResizable(false);
      
        
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- FILA 0: TÍTULO EN BARRA VERDE ---
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(ThemeManager.COLOR_BACKGROUND);
        JLabel lblTitulo = new JLabel("  AGREGAR CUOTA", SwingConstants.LEFT);
        lblTitulo.setFont(ThemeManager.TEXT_SUBTITLE);
        lblTitulo.setForeground(ThemeManager.COLOR_TEXT);
        lblTitulo.setPreferredSize(new Dimension(400, 40));
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panelPrincipal.add(panelTitulo, gbc);

        // --- FILA 1: DESCRIPCIÓN  ---
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblDesc = new JLabel("Descripción");
        lblDesc.setFont(ThemeManager.TEXT_NORMAL);
        lblDesc.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(lblDesc, gbc);


        gbc.gridx=1; gbc.gridwidth=2;
        inputDescription = new JTextField(14);
        inputDescription.setFont(ThemeManager.TEXT_NORMAL);
        inputDescription.setBackground(ThemeManager.COLOR_BACKGROUND);
        inputDescription.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(inputDescription, gbc);


        
        

        // --- FILA 2: MONTO ---
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblMonto = new JLabel("Monto ($)");
        lblMonto.setFont(ThemeManager.TEXT_NORMAL);
        lblMonto.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(lblMonto, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtMonto = new JTextField(15);
        txtMonto.setFont(ThemeManager.TEXT_NORMAL);
        txtMonto.setBackground(ThemeManager.COLOR_BACKGROUND);
        txtMonto.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(txtMonto, gbc);

        // --- FILA 3: FECHAS
        gbc.gridy = 3; gbc.gridx = 0;gbc.weightx = 0.0;
        JLabel lblFechaEmision = new JLabel("Emisión");
        lblFechaEmision.setFont(ThemeManager.TEXT_NORMAL);
        lblFechaEmision.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(lblFechaEmision, gbc);
        gbc.weightx = 0.5;

        gbc.gridx = 1; gbc.gridwidth = 1;
        panelPrincipal.add(jdcDesde,gbc);
        gbc.gridx=2; gbc.gridwidth = 1;
        panelPrincipal.add(jspHoraDesde,gbc);

        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(jspHoraDesde, "HH:mm:ss");
        jspHoraDesde.setEditor(editorDesde);
        panelPrincipal.add(jspHoraDesde, gbc);
        
        gbc.gridy = 4; gbc.gridx = 0; 
        JLabel lblFechaFinal = new JLabel("Limite");
        lblFechaFinal.setFont(ThemeManager.TEXT_NORMAL);
        lblFechaFinal.setForeground(ThemeManager.COLOR_TEXT);
        panelPrincipal.add(lblFechaFinal, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 1;
        panelPrincipal.add(jdcHasta,gbc);
        gbc.gridx=2; gbc.gridwidth = 1;
        panelPrincipal.add(jspHoraHasta,gbc);

        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(jspHoraHasta, "HH:mm:ss");
        jspHoraHasta.setEditor(editorHasta);
        panelPrincipal.add(jspHoraHasta, gbc);



        // --- FILA 5: BOTÓN DE ACCIÓN ---
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 8, 8, 8);
        
        JButton btnGuardar = new JButton("Agregar/Actualizar Cuota");
        btnGuardar.setFont(ThemeManager.TEXT_SUBTITLE);
        btnGuardar.setForeground(ThemeManager.COLOR_TEXT);
        btnGuardar.setBackground(ThemeManager.COLOR_PRIMARY);
        btnGuardar.setPreferredSize(new Dimension(350, 40));
        panelPrincipal.add(btnGuardar, gbc);

        // Evento Guardar / Validar
        btnGuardar.addActionListener(e -> {

            String strDesc = inputDescription.getText().trim();
            String montoStr = txtMonto.getText().trim();

                if(strDesc.isEmpty()){
                    JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN NO PUEDE ESTAR VACIA");
                    return;
                }

                if(!strDesc.matches("^[a-zA-Z0-9ñÑ ]+$")){
                    JOptionPane.showMessageDialog(this,"LA DESCRIPCIÓN DEBE SER ALFA-NUMERICO");
                    return;
                }   
                
            
            if (!montoStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Sistema Garita - ERROR X", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.Calendar calEmision = java.util.Calendar.getInstance();
            calEmision.setTime(jdcDesde.getDate());
            java.util.Calendar horaEmision = java.util.Calendar.getInstance();
            horaEmision.setTime((java.util.Date) jspHoraDesde.getValue());
            calEmision.set(java.util.Calendar.HOUR_OF_DAY, horaEmision.get(java.util.Calendar.HOUR_OF_DAY));
            calEmision.set(java.util.Calendar.MINUTE, horaEmision.get(java.util.Calendar.MINUTE));
            calEmision.set(java.util.Calendar.SECOND, horaEmision.get(java.util.Calendar.SECOND));
            java.sql.Timestamp tsEmision = new java.sql.Timestamp(calEmision.getTimeInMillis());


            java.util.Calendar calLimite = java.util.Calendar.getInstance();
            calLimite.setTime(jdcHasta.getDate());
            java.util.Calendar horaLimite = java.util.Calendar.getInstance();
            horaLimite.setTime((java.util.Date) jspHoraHasta.getValue());
            calLimite.set(java.util.Calendar.HOUR_OF_DAY, horaLimite.get(java.util.Calendar.HOUR_OF_DAY));
            calLimite.set(java.util.Calendar.MINUTE, horaLimite.get(java.util.Calendar.MINUTE));
            calLimite.set(java.util.Calendar.SECOND, horaLimite.get(java.util.Calendar.SECOND));
            java.sql.Timestamp tsLimite = new java.sql.Timestamp(calLimite.getTimeInMillis());

            Object[] valores = new Object[] {
                strDesc,                        
                Double.parseDouble(montoStr),   
                tsEmision,                     
                tsLimite,                       
                true                            
            };

        try {
                DB.comandoDML(queryInsert, valores);
                
                JOptionPane.showMessageDialog(this, "Cuota creada correctamente.", "Sistema Garita", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                this.menuPadre.Search(); 
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("La inserción falló, reteniendo la ventana.");
                
            }



            //region Debo el comprobante de las fehcas emision/limite
         
            dispose();
            this.menuPadre.Search(); 
        });

        setContentPane(panelPrincipal);
        pack();
        setLocationRelativeTo(framePadre); 
    }
}