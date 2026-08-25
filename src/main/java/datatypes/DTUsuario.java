package datatypes;

public class DTUsuario {
	private final String email;
	private final String nombre;

	public DTUsuario(String email, String nombre) {
		this.email = email;
		this.nombre = nombre;
	}

	public String getEmail() { return email; }
	public String getNombre() { return nombre; }
}
