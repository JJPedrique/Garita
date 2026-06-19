package Backend;
import java.awt.*;
import javax.swing.*;

public class ThemeManager {
    public static final Color COLOR_BACKGROUND_DARK  = Color.decode("#121212");
    public static final Color COLOR_BACKGROUND       = Color.decode("#1E1E1E");
    public static final Color COLOR_BACKGROUND_LIGHT = Color.decode("#2D2D2D");
    public static final Color COLOR_PRIMARY          = Color.decode("#428b13");
    public static final Color COLOR_SECONDARY        = Color.decode("#6ab848");
    public static final Color COLOR_ERROR            = Color.decode("#FF4D4D");
    public static final Color COLOR_WARNING          = Color.decode("#FFC107");
    public static final Color COLOR_INFO             = Color.decode("#4B0082");
    public static final Color COLOR_TEXT             = Color.decode("#FFFFFF");
    public static final Color COLOR_TEXT_DARK        = Color.decode("#000000");
    public static final Color COLOR_PLACEHOLDER      = Color.decode("#676767");
    public static final Color COLOR_INPUT            = Color.decode("#E9E9E9");
    public static final Color COLOR_LABEL            = Color.decode("#B8B8B8");

    public static final Font TEXT_TITLE              = new Font("Verdana", Font.BOLD, 20);
    public static final Font TEXT_SUBTITLE           = new Font("Verdana", Font.BOLD, 16);
    public static final Font TEXT_NORMAL             = new Font("Verdana", Font.PLAIN, 12);
    public static final Font TEXT_SMALL              = new Font("Verdana", Font.PLAIN, 10);

    public static JButton Button(String text){
        JButton newButton = new JButton(text);
        newButton.setFont(ThemeManager.TEXT_NORMAL);
        newButton.setForeground(ThemeManager.COLOR_TEXT);
        newButton.setBackground(ThemeManager.COLOR_PRIMARY);
        newButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newButton.setBackground(ThemeManager.COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                newButton.setBackground(ThemeManager.COLOR_PRIMARY);
            }
        });
        return newButton;
    }

    public static JLabel Label(String text){
        JLabel newLabel = new JLabel(text);
        newLabel.setFont(ThemeManager.TEXT_NORMAL);
        newLabel.setForeground(ThemeManager.COLOR_TEXT);        
        return newLabel;
    }

    public static JComboBox<String> StringComboBox(){
        JComboBox<String> newComboBox = new JComboBox<>();
        newComboBox.setFont(ThemeManager.TEXT_NORMAL);
        newComboBox.setForeground(ThemeManager.COLOR_TEXT_DARK);
        newComboBox.setBackground(ThemeManager.COLOR_SECONDARY);
        return newComboBox;
    }

    public static JPanel Panel(LayoutManager LM){
        JPanel newPanel = new JPanel(LM);
        newPanel.setBackground(ThemeManager.COLOR_BACKGROUND_LIGHT);
        return newPanel;
    }
}
