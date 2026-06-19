package Backend.BDD.DataInputs;
import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class BooleanInput extends Input{
    JLabel title;
    JComboBox<String> OptionsMenu;

    public BooleanInput(String label){
        this.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        title = new JLabel(label);
        title.setForeground(ThemeManager.COLOR_TEXT);
        title.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        title.setFont(ThemeManager.TEXT_NORMAL);
        this.add(title,gbc);

        gbc.gridx=1; gbc.weightx=1;    
        String Options[] = {"Verdadero","Falso"};
        OptionsMenu = new JComboBox<>(Options);
        OptionsMenu.setFont(ThemeManager.TEXT_NORMAL);
        OptionsMenu.setForeground(ThemeManager.COLOR_TEXT_DARK);
        OptionsMenu.setBackground(ThemeManager.COLOR_SECONDARY);
        this.add(OptionsMenu,gbc);

        gbc.gridx=2;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=2; gbc.weightx=0;
        BtnRemover.setFont(ThemeManager.TEXT_NORMAL);
        BtnRemover.setForeground(ThemeManager.COLOR_TEXT);
        BtnRemover.setBackground(ThemeManager.COLOR_PRIMARY);
        BtnRemover.setFont(ThemeManager.TEXT_SUBTITLE);
        this.add(BtnRemover,gbc);
        
        BtnRemover.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnRemover.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnRemover.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });
    }

    public String GetInput(){
        String strInput = OptionsMenu.getSelectedItem().toString();
        
        if(strInput.isEmpty()){return "";}

        return title.getText() + " = " + strInput;
    }
}
