import java.awt.*;
import javax.swing.*;

import Frontend.MenuPrincipal;

public class App {
<<<<<<< Updated upstream
    
=======
>>>>>>> Stashed changes
    static int WIDTH=1600,HEIGHT=900;

    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame("Sistema Garita");
        window.setSize(WIDTH,HEIGHT);
        window.setMinimumSize(new Dimension(WIDTH,HEIGHT));

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(new MenuPrincipal());
        
        window.setVisible(true);
    }
}
