package Backend.BDD.DataInputs;
import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class StringInput extends Input{
    JLabel title;
    JComboBox<String> OptionsMenu;
    JTextField input;

    public StringInput(String label){
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
        String Options[] = {"contiene","no contiene"};
        OptionsMenu = new JComboBox<>(Options);
        OptionsMenu.setFont(ThemeManager.TEXT_NORMAL);
        OptionsMenu.setForeground(ThemeManager.COLOR_TEXT_DARK);
        OptionsMenu.setBackground(ThemeManager.COLOR_SECONDARY);
        this.add(OptionsMenu,gbc);

        gbc.gridx=0;gbc.gridy=1; gbc.gridwidth=2;
        input =  new JTextField();
        input.setFont(ThemeManager.TEXT_NORMAL);
        input.setForeground(ThemeManager.COLOR_TEXT);
        input.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.add(input,gbc);

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
        String strInput = input.getText().trim();
        if(strInput.isEmpty()){return "";}
        if(!strInput.matches("^[a-zA-Z0-9]+$")){
            JOptionPane.showMessageDialog(this, title.getText() + " DEBE SER ALFA-NUMERICO");
            return "???";
        }

        if(OptionsMenu.getSelectedItem().toString().equals("=")){return title.getText() + " ILIKE '%" + strInput + "%'";}
        else{return title.getText() + " NOT ILIKE '%" + strInput + "%'";}

    }
}
