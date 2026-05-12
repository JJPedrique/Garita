package BDD.DataInputs;
import java.awt.*;
import javax.swing.*;

public class DecimalInput extends JPanel {
     JLabel title;
    JComboBox<String> OptionsMenu;
    JTextField input;
    JComboBox<String> FunctionMenu;

    public DecimalInput(String label){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        gbc.gridwidth = 2;gbc.weightx=1;
        title = new JLabel(label);
        this.add(title,gbc);

        gbc.gridy=1;gbc.weightx=0;gbc.gridx=0;gbc.gridwidth=1;
        String Options[] = {"=","!=",">",">=","<=",};
        OptionsMenu = new JComboBox<>(Options);
        this.add(OptionsMenu,gbc);

        gbc.gridx=1;gbc.weightx=1;
        input =  new JTextField();
        this.add(input,gbc);
    }

    public String GetInput(){
        if(input == null){return "";}
        String strInput = input.getText().trim();

        if(strInput.isEmpty()){return "";}
        if(!strInput.matches("\\d*\\.?\\d+")){
            JOptionPane.showMessageDialog(this, title.getText() + " DEBE SER DECIMAL");
            return "???";
        }

        return title.getText() + " " + OptionsMenu.getSelectedItem() + " "  + strInput;
    }
}
