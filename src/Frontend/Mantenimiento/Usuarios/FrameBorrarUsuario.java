package Frontend.Mantenimiento.Usuarios;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

import org.openpdf.text.alignment.HorizontalAlignment;

import Backend.ConexionPostgres;
import Backend.ThemeManager;



public class FrameBorrarUsuario extends JDialog {
    
    int id;
    String SQL = "UPDATE usuarios SET activo = ? WHERE id = ?;";


    public FrameBorrarUsuario(int newId){
        id = newId;
        this.setLayout(new BorderLayout());
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setTitle("Borrar Usuario");
        this.setSize(400, 200);
        this.setResizable(false);

        this.add(Top(),BorderLayout.NORTH);
        this.add(Center(),BorderLayout.CENTER);
        this.add(Bottom(),BorderLayout.SOUTH);

        this.setVisible(true);
    }

    JPanel Top(){
        JPanel newPanel = ThemeManager.Panel(new FlowLayout());
        newPanel.setBackground(ThemeManager.COLOR_PRIMARY);
        
        JLabel Titulo = ThemeManager.Label("Agregar Nuevo Usuario");
        Titulo.setForeground(ThemeManager.COLOR_TEXT);
        Titulo.setFont(ThemeManager.TEXT_TITLE);
        newPanel.add(Titulo);

        return newPanel;
    }

    JPanel Center(){
        JPanel newPanel = ThemeManager.Panel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;gbc.gridy=0;
        gbc.fill= GridBagConstraints.BOTH;
        gbc.weightx = 1;gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel Seguro = ThemeManager.Label("¿Seguro que quieres borrar este usuario?");
        Seguro.setFont(ThemeManager.TEXT_SUBTITLE);
        Seguro.setVerticalAlignment(SwingConstants.CENTER);
        Seguro.setHorizontalAlignment(SwingConstants.CENTER);

        newPanel.add(Seguro,gbc);
        gbc.gridy=1;
        JLabel Info = ThemeManager.Label("Se perderán los datos para siempre");
        Info.setVerticalAlignment(SwingConstants.CENTER);
        Info.setHorizontalAlignment(SwingConstants.CENTER);

        newPanel.add(Info,gbc);

        return newPanel;
    }
    
    JPanel Bottom(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;gbc.gridy=0;
        gbc.fill= GridBagConstraints.BOTH;
        gbc.weightx = 1;gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton BtnEliminar = ThemeManager.Button("Eliminar");
        BtnEliminar.setForeground(ThemeManager.COLOR_TEXT);
        BtnEliminar.setFont(ThemeManager.TEXT_TITLE);
        newPanel.add(BtnEliminar,gbc);
        BtnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {Eliminar();} catch (SQLException e1) {e1.printStackTrace();}
            }
        });

        gbc.gridx=1;
        JButton BtnCancelar = ThemeManager.Button("Cancelar");
        BtnCancelar.setForeground(ThemeManager.COLOR_TEXT);
        BtnCancelar.setFont(ThemeManager.TEXT_TITLE);
        newPanel.add(BtnCancelar,gbc);
        BtnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {Cancelar();}
        });

        return newPanel;
    }

    void Cancelar(){this.dispose();}

    void Eliminar() throws SQLException{

        ConexionPostgres.comandoDML(SQL,new Object[]{false,id});
        ThemeManager.MostrarMensajeExito(this,"EXITO - Usuario agregado exitosamente.");
        this.dispose();
    }
}
