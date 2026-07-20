package Frontend.Reportes.DataInputs;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import Backend.ThemeManager;

public class DateInput extends Input{

    JLabel title;
    JDateChooser FromDate = new JDateChooser(); 
    JSpinner FromTime = new JSpinner(new SpinnerDateModel());
    JDateChooser ToDate = new JDateChooser(); 
    JSpinner ToTime = new JSpinner(new SpinnerDateModel());
    
    public DateInput(String label){
        SetupDateChooser(FromDate);
        SetupDateChooser(ToDate);
        SetupTimeSpinner(FromTime);
        SetupTimeSpinner(ToTime);
        
        this.setBackground(ThemeManager.COLOR_BACKGROUND);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weighty=1;gbc.weightx=1;
        gbc.insets = new Insets(5, 5,0,5);

        title = new JLabel(label);
        title.setForeground(ThemeManager.COLOR_TEXT);
        this.add(title,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.gridwidth=1;
        JLabel From = ThemeManager.Label("Desde");
        this.add(From,gbc);


        
        gbc.gridy=2;gbc.gridx=0;
        JLabel To = ThemeManager.Label("Hasta");
        this.add(To,gbc);
    
        gbc.gridy=1;gbc.weightx=1;
        
        gbc.gridx=1;
        FromDate.setLocale(Locale.getDefault());
        this.add(FromDate,gbc);

        gbc.gridx=2;
        FromTime.setEditor(new JSpinner.DateEditor(FromTime, "HH:mm:ss"));
        this.add(FromTime,gbc);
        
        gbc.gridy=2;gbc.gridx=1;
        ToDate.setLocale(Locale.getDefault());
        this.add(ToDate,gbc);

        gbc.gridx=2;
        ToTime.setEditor(new JSpinner.DateEditor(ToTime, "HH:mm:ss"));
        this.add(ToTime,gbc);
    }

    void SetupDateChooser(JDateChooser JDC) {
        JDC.setDateFormatString("dd/MM/yyyy");
        JDC.setOpaque(false);

        JTextField tfDate = (JTextField) JDC.getDateEditor().getUiComponent();
        tfDate.setEditable(false);
        tfDate.setBackground(ThemeManager.COLOR_INPUT);
        tfDate.setForeground(ThemeManager.COLOR_TEXT_DARK);
        tfDate.setFont(ThemeManager.TEXT_NORMAL);
        tfDate.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        tfDate.setPreferredSize(new Dimension(80, 25));

        JButton bCalendario = JDC.getCalendarButton();
        bCalendario.setBackground(ThemeManager.COLOR_PRIMARY);
        bCalendario.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        bCalendario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bCalendario.setPreferredSize(new Dimension(30, 25));
    }

    void SetupTimeSpinner(JSpinner JTime) {
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(JTime, "HH:mm:ss");
        JTime.setEditor(timeEditor);
        JTime.setBorder(BorderFactory.createEmptyBorder());
        JTime.setBackground(ThemeManager.COLOR_INPUT);

        JTextField TF = timeEditor.getTextField();
        TF.setEditable(false); 
        
        TF.setBackground(ThemeManager.COLOR_INPUT);
        TF.setForeground(ThemeManager.COLOR_TEXT_DARK);
        TF.setFont(ThemeManager.TEXT_NORMAL);
        TF.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        TF.setHorizontalAlignment(JTextField.CENTER);
    }

    @Override
    public String GetCondition(){
        Date F_Date = FromDate.getDate();
        Date T_Date = ToDate.getDate();
    
        if (F_Date == null && T_Date == null) {return "";}
        else if (F_Date != null && T_Date == null) {return "\""+ title.getText() + "\" >= ?";}
        else if (F_Date == null && T_Date != null) {return "\"" + title.getText() + "\" <= ?";}
        else {return "\"" + title.getText() + "\" >= ? AND \"" + title.getText() + "\" <= ?";  }
    }

    @Override
    public String GetValue(){
        try {
            SimpleDateFormat formatoD = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoT = new SimpleDateFormat("HH:mm:ss");

            String F_Time_str = formatoT.format(FromTime.getValue());
            String T_Time_str = formatoT.format(ToTime.getValue());
            Date F_Date = FromDate.getDate();
            Date T_Date = ToDate.getDate();

            if (F_Date == null && T_Date == null) {
                return "";
            }
            else if (F_Date != null && T_Date == null) {
                String F_Date_str =  formatoD.format(F_Date);
                return F_Date_str + " " +  F_Time_str;
            }
            else if (F_Date == null && T_Date != null) {
                String T_Date_str =  formatoD.format(T_Date);
                return T_Date_str + " " +  T_Time_str;
            }
            else {
                String F_Date_str =  formatoD.format(F_Date);
                String T_Date_str =  formatoD.format(T_Date);
                return F_Date_str + " " +  F_Time_str + "\n" + T_Date_str + " " + T_Time_str;  
            }
        }catch (Exception e) {
            ThemeManager.MostrarMensajeError(this,"ERROR - Se Ingreso un mal formato de Fecha/Hora");
            return "???";
        } 
    }
}
