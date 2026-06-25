package Frontend.Reportes.DataInputs;
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

        title = ThemeManager.Label(label);
        this.add(title,gbc);

        gbc.gridx=1; gbc.weightx=1;    
        String Options[] = {"Contiene","No contiene"};
        OptionsMenu = ThemeManager.StringComboBox();
        for (String o : Options) {OptionsMenu.addItem(o);}
        this.add(OptionsMenu,gbc);

        gbc.gridx=0;gbc.gridy=1; gbc.gridwidth=2;
        input =  ThemeManager.Textfield();
        this.add(input,gbc);

        gbc.gridx=2;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=2; gbc.weightx=0;
        this.add(BtnRemover,gbc);     
    }
    
    @Override
    public String GetCondition(){
        if(OptionsMenu.getSelectedItem().toString() == "Contiene"){ return "\"" +title.getText() +"\" LIKE ?";}
        else{return title.getText() + " NOT ILIKE ?";}       
    }

    @Override
    public String GetValue(){
        String strInput = input.getText().trim();
        if(strInput.isEmpty()){return "";}

        if(!strInput.matches("^[a-zA-Z0-9]+$")){
            JOptionPane.showMessageDialog(this, title.getText() + " DEBE SER ALFA-NUMERICO");
            return "???";
        }

        return "%"+strInput+"%";
    }
}
