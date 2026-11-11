package publicadores;

public class UsuarioWS {
    private String email;
    private String nombre;
    private TipoUsuario tipo;

    public UsuarioWS() {}
    public String getEmail() { return email; }
    public void setEmail(String valor) { this.email = valor; }
    public String getNombre() { return nombre; }
    public void setNombre(String valor) { this.nombre = valor; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario valor) { this.tipo = valor; }
}
