package Frontend.Reportes.DataInputs;
import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class StringInput extends Input{
    JLabel title;
    JComboBox<String> OptionsMenu;
    JTextField input =  ThemeManager.Textfield("Solo Letras y Espacios");

    public StringInput(String label){
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        title = new JLabel(label);
        title.setForeground(ThemeManager.COLOR_TEXT);;
        this.add(title,gbc);

        gbc.gridx=1; gbc.weightx=1;    
        String Options[] = {"Contiene","No contiene"};
        OptionsMenu = ThemeManager.StringComboBox();
        for (String o : Options) {OptionsMenu.addItem(o);}
        this.add(OptionsMenu,gbc);

        gbc.gridx=0;gbc.gridy=1; gbc.gridwidth=2;        
        this.add(input,gbc);
    }
    
    @Override
    public String GetCondition(){
        if(OptionsMenu.getSelectedItem().toString() == "Contiene"){ return "\"" +title.getText() +"\" LIKE ?";}
        else{return "\""+title.getText() + "\" NOT ILIKE ?";}       
    }

    @Override
    public String GetValue(){
        String strInput = input.getText().trim();
        if(strInput.isEmpty()){return "";}

        if(!strInput.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ0-9\\s]+$")){
            ThemeManager.MostrarMensajeError(this,"El campo " + title.getText() + " solo debe contener letras, espacios y números.");
            return "???";
        }

        return "%"+strInput+"%";
    }
}
