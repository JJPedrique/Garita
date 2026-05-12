import javax.swing.*;
import Reportes.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame("Garita");
        window.setSize(500,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(new MenuReporte());
        
        window.setVisible(true);
        System.out.println("Hello, World!");
    }
}
