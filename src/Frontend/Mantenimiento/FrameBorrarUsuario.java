package Frontend.Mantenimiento;
import java.awt.*;
import javax.swing.*;


public class FrameBorrarUsuario extends JDialog {
    public FrameBorrarUsuario(){
        this.setLayout(new BorderLayout());
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setTitle("Borrar Usuario");
        this.setSize(400, 300);
        this.setResizable(false);

        this.add(Top(),BorderLayout.NORTH);
        this.add(Center(),BorderLayout.CENTER);
        this.add(Bottom(),BorderLayout.SOUTH);

        this.setVisible(true);
    }

    JPanel Top(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        return newPanel;
    }

    JPanel Center(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        return newPanel;
    }
    
    JPanel Bottom(){
        JPanel newPanel = new JPanel(new GridBagLayout());
        return newPanel;
    }
}
