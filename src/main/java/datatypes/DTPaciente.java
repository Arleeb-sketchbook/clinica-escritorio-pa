package datatypes;

public class DTPaciente extends DTUsuario {
	private final String mutualista;

	public DTPaciente(String email, String nombre, String mutualista) {
		super(email, nombre);
		this.mutualista = mutualista;
	}

	public String getMutualista() { return mutualista; }
}
