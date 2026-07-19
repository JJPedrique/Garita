package Frontend.Reportes.DataInputs;
import java.awt.*;
import javax.swing.*;

import Backend.ThemeManager;

public class DecimalInput extends Input {
    JLabel title;
    JComboBox<String> OptionsMenu;
    JTextField input = ThemeManager.Textfield("Solo Decimales...");

    public DecimalInput(String label){
        this.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        title = ThemeManager.Label(label);;
        this.add(title,gbc);

        gbc.gridx=1; gbc.weightx=1;    
        String Options[] = {"Es igual a","Es diferente a","Es mayor que","Es mayor igual que","Es menor que","Es menor igual que"};
        OptionsMenu = ThemeManager.StringComboBox();
        for (String o : Options) {OptionsMenu.addItem(o);}
        this.add(OptionsMenu,gbc);

        gbc.gridx=0;gbc.gridy=1; gbc.gridwidth=2;
        this.add(input,gbc);

        gbc.gridx=2;gbc.gridy=0;gbc.gridwidth=1;gbc.gridheight=2; gbc.weightx=0;
        this.add(BtnRemover,gbc);
    }

    @Override
    public String GetCondition(){
        switch (OptionsMenu.getSelectedItem().toString()) {
            case "Es igual a": return "\"" +title.getText() +"\" = ?";
            case "Es diferente a": return "\"" +title.getText() +"\" != ?";
            case "Es mayor que": return "\"" +title.getText() +"\" > ?";
            case "Es mayor igual que": return "\"" +title.getText() +"\" >= ?";
            case "Es menor que": return "\"" +title.getText() +"\" < ?";        
            case "Es menor igual que": return "\"" +title.getText() +"\" <= ?";
            default: return "???";
        }     
    }

    @Override
    public String GetValue(){
        if(input == null){return "";}
        String strInput = input.getText().trim();
        if(strInput.isEmpty()){return "";}

        if(!strInput.matches("\\d*\\[.,]?\\d+")){
            ThemeManager.MostrarMensajeError(this,"El campo " + title.getText() + " tiene que ser númerico, decimal y positivo");
            return "???";
        }
        return strInput;
    }
}
