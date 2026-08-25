package logica;

import jakarta.persistence.Entity;

@Entity
public class Medico extends Usuario {

	private String especialidad;

	protected Medico() {
	}

	public Medico(String email, String nombre, String password, String especialidad) {
		super(email, nombre, password);
		this.especialidad = especialidad;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
}
