package Backend.BDD.DataInputs;
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
        this.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.gridy=0;gbc.gridx=0;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5,0,5);

        gbc.gridwidth=3;
        title = new JLabel(label);
        title.setForeground(ThemeManager.COLOR_TEXT);
        title.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        title.setFont(ThemeManager.TEXT_NORMAL);
        this.add(title,gbc);

        gbc.gridy=1;gbc.gridx=0;gbc.gridwidth=1;
        JLabel From = new JLabel("Desde");
        From.setForeground(ThemeManager.COLOR_TEXT);
        From.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        From.setFont(ThemeManager.TEXT_NORMAL);
        this.add(From,gbc);

        gbc.gridy=2;gbc.gridx=0;
        JLabel To = new JLabel("Hasta");
        To.setForeground(ThemeManager.COLOR_TEXT);
        To.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        To.setFont(ThemeManager.TEXT_NORMAL);
        this.add(To,gbc);
    
        gbc.gridy=1;gbc.weightx=1;
        
        gbc.gridx=1;
        FromDate = new JDateChooser();
        FromDate.setLocale(Locale.getDefault());
        FromDate.setForeground(ThemeManager.COLOR_TEXT);
        FromDate.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        FromDate.setFont(ThemeManager.TEXT_NORMAL);
        this.add(FromDate,gbc);

        gbc.gridx=2;
        FromTime = new JSpinner(new SpinnerDateModel());
        FromTime.setEditor(new JSpinner.DateEditor(FromTime, "HH:mm:ss"));
        FromTime.setForeground(ThemeManager.COLOR_TEXT);
        FromTime.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        FromTime.setFont(ThemeManager.TEXT_NORMAL);
        this.add(FromTime,gbc);
        
        gbc.gridy=2;gbc.gridx=1;
        ToDate = new JDateChooser();
        ToDate.setLocale(Locale.getDefault());
        ToDate.setForeground(ThemeManager.COLOR_TEXT);
        ToDate.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        ToDate.setFont(ThemeManager.TEXT_NORMAL);
        this.add(ToDate,gbc);

        gbc.gridx=2;
        ToTime = new JSpinner(new SpinnerDateModel());
        ToTime.setEditor(new JSpinner.DateEditor(ToTime, "HH:mm:ss"));
        ToTime.setForeground(ThemeManager.COLOR_TEXT);
        ToTime.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        ToTime.setFont(ThemeManager.TEXT_NORMAL);
        this.add(ToTime,gbc);

        gbc.gridx=3;gbc.gridy=1;gbc.gridwidth=1;gbc.gridheight=2; gbc.weightx=0;
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
