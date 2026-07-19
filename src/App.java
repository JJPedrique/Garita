import java.awt.*;
import javax.swing.*;

import Backend.ConexionPostgres;
import Frontend.MenuPrincipal;
import Frontend.InicioSesion.MenuInicioSesion;

public class App {
    
    static int WIDTH=1600,HEIGHT=900;
    public static void main(String[] args) throws Exception {
        ConexionPostgres.conexion = ConexionPostgres.conectar();
        ConexionPostgres.InitDatabase();

        JFrame window = new JFrame("Sistema Garita");
        window.setSize(WIDTH,HEIGHT);
        window.setMinimumSize(new Dimension(WIDTH,HEIGHT));

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(new MenuPrincipal());
        //window.add(new MenuInicioSesion());
        
        window.setVisible(true);
    }
}
