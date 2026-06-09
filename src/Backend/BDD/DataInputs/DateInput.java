package Backend.BDD.DataInputs;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

public class DateInput extends JPanel{

    JLabel title;
    JDateChooser FromDate;JSpinner FromTime;
    JDateChooser ToDate;JSpinner ToTime;
    
    public DateInput(String label){
        this.setLayout(new GridBagLayout());
        this.setToolTipText(label);
        
        GridBagConstraints gbc =  new GridBagConstraints();
        
        gbc.gridy=0;gbc.gridx=0;
        gbc.insets = new Insets(1,1,1,1);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.weightx=1;gbc.gridwidth=3;
        title = new JLabel(label);
        this.add(title,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.gridwidth=1;
        JLabel From = new JLabel("Desde");
        this.add(From,gbc);

        gbc.gridy=2;gbc.gridx=0;
        JLabel To = new JLabel("Hasta");
        this.add(To,gbc);
    
        gbc.gridy=1;gbc.weightx=1;
        
        gbc.gridx=1;
        FromDate = new JDateChooser();
        FromDate.setLocale(Locale.getDefault());
        this.add(FromDate,gbc);

        gbc.gridx=2;
        FromTime = new JSpinner(new SpinnerDateModel());
        FromTime.setEditor(new JSpinner.DateEditor(FromTime, "HH:mm:ss"));
        this.add(FromTime,gbc);
        
        gbc.gridy=2;gbc.gridx=1;
        ToDate = new JDateChooser();
        ToDate.setLocale(Locale.getDefault());
        this.add(ToDate,gbc);

        gbc.gridx=2;
        ToTime = new JSpinner(new SpinnerDateModel());
        ToTime.setEditor(new JSpinner.DateEditor(ToTime, "HH:mm:ss"));
        this.add(ToTime,gbc);
    }

    public String GetInput(){
        if(FromDate.getDate() == null && ToDate.getDate() == null){return "";}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

        if(FromDate.getDate() != null && ToDate.getDate() != null){
            return title.getText() + " >= '" + sdf.format(FromDate.getDate()) + " " +  sdf2.format(FromTime.getValue()) + "'" + " AND " 
            + title.getText() + " <= '" + sdf.format(ToDate.getDate()) + " " + sdf2.format(ToTime.getValue()) + "'";     
        }
        else if(FromDate.getDate() != null){
            return title.getText() + " >= '" + sdf.format(FromDate.getDate()) + " " +  sdf2.format(FromTime.getValue()) + "'";
        }
        else{
           return title.getText() + " <= '" + sdf.format(ToDate.getDate()) + " " +  sdf2.format(ToTime.getValue()) + "'";
        }  
    }
}
