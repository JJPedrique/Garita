package Frontend.Mantenimiento.Usuarios;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import Backend.ThemeManager;


public class FrameModificarUsuario extends JDialog{
    
    JTextField InputNombre = ThemeManager.Textfield();
    JTextField InputApellido = ThemeManager.Textfield();
    JTextField InputCedula = ThemeManager.Textfield();
    JTextField InputTelefono = ThemeManager.Textfield();
    JTextField InputClave = ThemeManager.Textfield();

    public FrameModificarUsuario(){
         this.setLayout(new BorderLayout());
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setTitle("Actualizar Usuario");
        this.setSize(400, 300);
        this.setResizable(false);

        this.add(Top(),BorderLayout.NORTH);
        this.add(Center(),BorderLayout.CENTER);
        this.add(Bottom(),BorderLayout.SOUTH);

        this.setVisible(true);
    }

    JPanel Top(){
        JPanel newPanel = new JPanel(new FlowLayout());
        newPanel.setBackground(ThemeManager.COLOR_PRIMARY);
        
        JLabel Titulo = ThemeManager.Label("Actualizar Usuario");
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
            public void actionPerformed(ActionEvent e) {Modificar();}
        });
        return newPanel;
    }

    void Modificar(){
        this.dispose();
    } 
}
