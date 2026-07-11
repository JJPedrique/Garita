package Backend;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private String cedula;
    private String nombre;

  
    private SesionUsuario() {}

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public void iniciarSesion(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
    }

    public void cerrarSesion() {
        this.cedula = null;
        this.nombre = null;
    }


    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
}