import java.awt.*;
import javax.swing.*;

import Frontend.MenuPrincipal;
import Frontend.InicioSesion.MenuInicioSesion;

public class App {
    
    static int WIDTH=500,HEIGHT=500;

    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame("Sistema Garita");
        window.setSize(WIDTH,HEIGHT);
        window.setMinimumSize(new Dimension(WIDTH,HEIGHT));

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(new MenuInicioSesion());
        
        window.setVisible(true);
    }
}
