package cl.academia.academiaapp.sqlLite;

/**
 * Created by miguel on 21-07-16.
 */
public class UsuarioPojo {
    private String usuario;
    private String nombre;
    private String apellido;
    private String password;

    public UsuarioPojo() {

    }

    public UsuarioPojo(String usuario, String nombre, String apellido){
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
