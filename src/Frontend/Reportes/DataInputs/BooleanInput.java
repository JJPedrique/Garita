package Frontend.Reportes.DataInputs;
import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class BooleanInput extends Input{
    JLabel title;
    JComboBox<String> OptionsMenu;

    public BooleanInput(String label){
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        title = ThemeManager.Label(label);
        this.add(title,gbc);

        gbc.gridx=1; gbc.weightx=1;    
        String Options[] = {"","Verdadero","Falso"};
        OptionsMenu = ThemeManager.StringComboBox();
        for(String k : Options){OptionsMenu.addItem(k);}             
        this.add(OptionsMenu,gbc);
    }

    @Override
    public String GetCondition(){return "\"" +title.getText() +"\" = ?";}

    @Override
    public String GetValue(){        
        String strInput = OptionsMenu.getSelectedItem().toString();
        if(strInput.isEmpty()){return "";}
        return strInput;
    }
}
