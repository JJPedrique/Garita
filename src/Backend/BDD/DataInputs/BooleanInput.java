package Backend.BDD.DataInputs;
import java.awt.*;
import javax.swing.*;

public class BooleanInput extends JPanel{
    JLabel title;
    JComboBox<String> input;

    public BooleanInput(String label){
        this.setLayout(new GridBagLayout());
        this.setToolTipText(label);
        
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;gbc.weightx=1;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,1,1,1);

        title = new JLabel(label);
        this.add(title,gbc);

        gbc.gridy=1;
        String Values[] = {"","true","false"};
        input = new JComboBox<>(Values);
        this.add(input,gbc);
    }

    public String GetInput(){
        String strInput = input.getSelectedItem().toString();
        
        if(strInput.isEmpty()){return "";}

        return title.getText() + " = " + strInput;
    }
}
