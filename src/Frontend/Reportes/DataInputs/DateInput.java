package Frontend.Reportes.DataInputs;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import Backend.ThemeManager;

public class DateInput extends Input{

    JLabel title;
    JDateChooser FromDate; JSpinner FromTime;
    JDateChooser ToDate; JSpinner ToTime;
    
    public DateInput(String label){
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        gbc.gridwidth=3;
        title = ThemeManager.Label(label);
        this.add(title,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.gridwidth=1;
        JLabel From = ThemeManager.Label("Desde");
        this.add(From,gbc);

        gbc.gridy=2;gbc.gridx=0;
        JLabel To = ThemeManager.Label("Hasta");
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
   
    @Override
    public String GetCondition(){
        if(FromDate.getDate() != null && ToDate.getDate() != null){return "\"" + title.getText() + "\" >= ? AND \"" + title.getText() + "\" <= ?";     }
        else if(FromDate.getDate() != null){return "\""+ title.getText() + "\" >= ?";}
        else{return "\"" + title.getText() + "\" <= ?";} 
    }

    @Override
    public String GetValue(){
        try {
            if(FromDate.getDate() == null && ToDate.getDate() == null){return "";}
            SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
            
            if(FromDate.getDate() != null && ToDate.getDate() != null){
                return DATE.format(FromDate.getDate()) + " " +  TIME.format(FromTime.getValue()) + "\n" 
                + DATE.format(ToDate.getDate()) + " " + TIME.format(ToTime.getValue());     
            }else if(FromDate.getDate() != null){return DATE.format(FromDate.getDate()) + " " +  TIME.format(FromTime.getValue());}
            else{return title.getText() + " <= '" + DATE.format(ToDate.getDate()) + " " +  TIME.format(ToTime.getValue());}
        } catch (Exception e) {
            ThemeManager.MostrarMensajeError(this,"ERROR - Se Ingreso un mal formato de Fecha/Hora");
            return "???";
        }
    }
}
