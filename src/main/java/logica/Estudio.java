package logica;

import jakarta.persistence.Entity;

@Entity
public class Estudio extends Prestacion {

	private int duracionMinutos;

	protected Estudio() {
	}

	public Estudio(String nombre, double precio, Franja franja, int duracionMinutos) {
		super(nombre, precio, franja);
		this.duracionMinutos = duracionMinutos;
	}

	public int getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(int duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}
}
