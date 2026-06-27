package Frontend.Mantenimiento.Usuarios;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Backend.ThemeManager;

public class FrameAgregarUsuario extends JDialog {

    JTextField InputNombre = ThemeManager.Textfield();
    JTextField InputApellido = ThemeManager.Textfield();
    JTextField InputCedula = ThemeManager.Textfield();
    JTextField InputTelefono = ThemeManager.Textfield();
    JComboBox<String> InputRol = ThemeManager.StringComboBox();
    JTextField InputClave = ThemeManager.Textfield();

    String ROLES[] = {"Vigilancia","Administrador","Junta"};

    public FrameAgregarUsuario(){
        this.setLayout(new BorderLayout());
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setTitle("Agregar Usuario");
        this.setSize(400, 350);
        this.setResizable(false);

        this.add(Top(),BorderLayout.NORTH);
        JPanel center = Center();
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(center,BorderLayout.CENTER);
        this.add(Bottom(),BorderLayout.SOUTH);

        this.setVisible(true);
    }

    JPanel Top(){
        JPanel newPanel = new JPanel(new FlowLayout());
        newPanel.setBackground(ThemeManager.COLOR_PRIMARY);
        
        JLabel Titulo = ThemeManager.Label("Agregar Nuevo Usuario");
        Titulo.setForeground(ThemeManager.COLOR_TEXT);
        Titulo.setFont(ThemeManager.TEXT_TITLE);
        newPanel.add(Titulo);

        return newPanel;
    }

    JPanel Center(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;gbc.gridy=0;
        gbc.fill= GridBagConstraints.BOTH;
        gbc.weightx = 0;gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel Nombre  = ThemeManager.Label("Nombre");
        newPanel.add(Nombre,gbc);

        gbc.gridx=1;gbc.weightx = 1;
        newPanel.add(InputNombre,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.weightx = 0;
        JLabel Apellido  = ThemeManager.Label("Apellido");
        newPanel.add(Apellido,gbc);
        
        gbc.gridx=1;gbc.weightx = 1;
        newPanel.add(InputApellido,gbc);

        gbc.gridy=2;gbc.gridx=0;gbc.weightx = 0;
        JLabel Cedula  = ThemeManager.Label("Cedula");
        newPanel.add(Cedula,gbc);

        gbc.gridx=1;gbc.weightx = 1;
        newPanel.add(InputCedula,gbc);        

        gbc.gridy=3;gbc.gridx=0;gbc.weightx = 0;
        JLabel Telefono  = ThemeManager.Label("Telefono");
        newPanel.add(Telefono,gbc);

        gbc.gridx=1;gbc.weightx = 1;
        newPanel.add(InputTelefono,gbc);

        gbc.gridy=4;gbc.gridx=0;gbc.weightx = 0;
        JLabel Rol  = ThemeManager.Label("Rol");
        newPanel.add(Rol,gbc);

        gbc.gridx=1;gbc.weightx = 1;
        for (String R : ROLES) {InputRol.addItem(R);}
        newPanel.add(InputRol,gbc);

        gbc.gridy=5;gbc.gridx=0;gbc.weightx = 0;
        JLabel Clave  = ThemeManager.Label("Clave");
        newPanel.add(Clave,gbc);

        gbc.gridx=1;gbc.weightx = 1;
        newPanel.add(InputClave,gbc);

        return newPanel;
    }
    
    JPanel Bottom(){
        JPanel newPanel = new JPanel(new FlowLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);
        
        JButton BtnAgregar = ThemeManager.Button("Agregar Nuevo Usuario");
        BtnAgregar.setForeground(ThemeManager.COLOR_TEXT);
        BtnAgregar.setFont(ThemeManager.TEXT_TITLE);
        newPanel.add(BtnAgregar);
        BtnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {Agregar();}
        });
        return newPanel;
    }

    void Agregar(){

    String strNombre = InputNombre.getText().trim();
    String strApellido = InputApellido.getText().trim();
    String strCedula = InputCedula.getText().trim();
    String strTelefono = InputTelefono.getText().trim();
    String strRol = InputRol.getSelectedItem().toString();
    String strClave = InputClave.getText().trim();

    if(strNombre.isEmpty())



        this.dispose();
    }
}
