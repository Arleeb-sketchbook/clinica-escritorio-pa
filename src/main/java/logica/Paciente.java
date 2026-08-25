package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Paciente extends Usuario {

	private String mutualista;

	@OneToMany(mappedBy = "paciente", orphanRemoval = true)
	private final List<Seguido> seguidas = new ArrayList<>();

	@OneToMany(mappedBy = "paciente")
	private final List<OrdenMedica> ordenes = new ArrayList<>();

	protected Paciente() {
	}

	public Paciente(String email, String nombre, String password, String mutualista) {
		super(email, nombre, password);
		this.mutualista = mutualista;
	}

	public String getMutualista() {
		return mutualista;
	}

	public void setMutualista(String mutualista) {
		this.mutualista = mutualista;
	}

	public List<Seguido> getSeguidas() {
		return Collections.unmodifiableList(seguidas);
	}

	public List<OrdenMedica> getOrdenes() {
		return Collections.unmodifiableList(ordenes);
	}
}
