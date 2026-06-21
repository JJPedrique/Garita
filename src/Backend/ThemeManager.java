package Backend;
import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.table.TableModel;

public class ThemeManager {
    public static final Color COLOR_BACKGROUND_DARK  = Color.decode("#121212");
    public static final Color COLOR_BACKGROUND       = Color.decode("#1E1E1E");
    public static final Color COLOR_BACKGROUND_LIGHT = Color.decode("#2D2D2D");
    public static final Color COLOR_PRIMARY          = Color.decode("#428b13");
    public static final Color COLOR_SECONDARY        = Color.decode("#6ab848");
    public static final Color COLOR_ERROR            = Color.decode("#FF3131");
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

    public static final int BORDER_RADIUS_PX = 16;
    public static final int ICON_WIDTH_PX = 24;
    public static final int ICON_HEIGHT_PX = 24;

    public static JButton Button(String text){
        JButton newButton = new JButton(text);
        newButton.setFont(TEXT_NORMAL);
        newButton.setForeground(COLOR_TEXT);
        newButton.setBackground(COLOR_PRIMARY);
        newButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newButton.setBackground(COLOR_SECONDARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                newButton.setBackground(COLOR_PRIMARY);
            }
        });
        return newButton;
    }

    public static JLabel Label(String text){
        JLabel newLabel = new JLabel(text);
        newLabel.setFont(TEXT_NORMAL);
        newLabel.setForeground(COLOR_TEXT);        
        return newLabel;
    }

    public static JComboBox<String> StringComboBox(){
        JComboBox<String> newComboBox = new JComboBox<>();
        newComboBox.setFont(TEXT_NORMAL);
        newComboBox.setForeground(COLOR_TEXT_DARK);
        newComboBox.setBackground(COLOR_SECONDARY);
        return newComboBox;
    }

    public static JPanel Panel(LayoutManager LM){
        JPanel newPanel = new JPanel(LM);
        newPanel.setBackground(COLOR_BACKGROUND_LIGHT);
        return newPanel;
    }

    public static JScrollPane ScrollPanel(JTable Table){
        JScrollPane newScrollPane = new JScrollPane(Table);
        newScrollPane.setBackground(COLOR_BACKGROUND_LIGHT);
        newScrollPane.setForeground(COLOR_BACKGROUND);
        return newScrollPane;
    }


    public static JTextField Textfield(){
        JTextField newTextField = new JTextField();
        newTextField.setFont(TEXT_NORMAL);
        newTextField.setForeground(COLOR_TEXT);
        newTextField.setBackground(COLOR_BACKGROUND);
        return newTextField;
    }

    public static JTable Table(TableModel DTM){
        JTable newTable = new JTable();
        newTable.setModel(DTM);
        newTable.setForeground(COLOR_TEXT);
        newTable.setBackground(COLOR_BACKGROUND);
        newTable.setFont(TEXT_NORMAL);
        return newTable;
    }

    public static ImageIcon SetImgIcon(String resourcePath, int width, int height) {
        try {
            File file = new File(resourcePath);
            if (file.exists()) {
                ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el icono " + resourcePath + ": " + e.getMessage());
        }
        return null;
    }
}
