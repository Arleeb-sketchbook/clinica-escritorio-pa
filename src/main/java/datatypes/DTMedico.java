package datatypes;

public class DTMedico extends DTUsuario {
	private final String especialidad;

	public DTMedico(String email, String nombre, String especialidad) {
		super(email, nombre);
		this.especialidad = especialidad;
	}

	public String getEspecialidad() { return especialidad; }
}
