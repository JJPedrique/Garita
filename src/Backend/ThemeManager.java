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

    public static JButton SideBarButton(String texto){
        JButton btn = ThemeManager.Button(texto);
        btn.setMaximumSize(new Dimension(175, 40));
        btn.setFont(ThemeManager.TEXT_SUBTITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
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

    public static void MostrarMensajeError(JPanel JP, String msg) {
        JDialog customDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(JP), "Sistema Garita - ERROR", Dialog.ModalityType.APPLICATION_MODAL);
        configurarDialogMensaje(JP, customDialog, msg, new Color(200, 50, 50), "X");
    }

    public static void MostrarMensajeExito(JPanel JP, String msg) {
        JDialog customDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(JP), "Sistema Garita - EXITO", Dialog.ModalityType.APPLICATION_MODAL);
        configurarDialogMensaje(JP, customDialog, msg, new Color(70, 140, 35), "i");
    }
    
    public static void configurarDialogMensaje(JPanel JP, JDialog dialog, String msg, Color bgButton, String iconChar) {
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(JP);
        
        JPanel pRoot = new JPanel(new GridBagLayout());
        pRoot.setBackground(ThemeManager.COLOR_BACKGROUND_DARK);
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel lIcon = new JLabel(iconChar, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgButton);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lIcon.setPreferredSize(new Dimension(40, 40));
        lIcon.setForeground(ThemeManager.COLOR_TEXT);
        lIcon.setFont(ThemeManager.TEXT_SUBTITLE);

        JLabel lMsg = new JLabel(msg);
        lMsg.setForeground(Color.WHITE);
        lMsg.setFont(ThemeManager.TEXT_NORMAL);

        JButton btnAceptar = ThemeManager.Button("Aceptar");
        btnAceptar.setBackground(bgButton);
        btnAceptar.setPreferredSize(new Dimension(120, 35));
        btnAceptar.addActionListener(e -> dialog.dispose());

        c.insets = new Insets(15, 20, 10, 10);
        c.gridx = 0; c.gridy = 0; pRoot.add(lIcon, c);
        c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL; pRoot.add(lMsg, c);
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 20, 20, 20); pRoot.add(btnAceptar, c);

        dialog.add(pRoot);
        dialog.setVisible(true);
    }
}
