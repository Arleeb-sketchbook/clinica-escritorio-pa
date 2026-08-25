package logica;

import jakarta.persistence.Entity;

@Entity
public class Terapia extends Prestacion {

	private boolean requiereDerivacion;
	private int cantidadSesiones;

	protected Terapia() {
	}

	public Terapia(String nombre, double precio, Franja franja,
			boolean requiereDerivacion, int cantidadSesiones) {
		super(nombre, precio, franja);
		this.requiereDerivacion = requiereDerivacion;
		this.cantidadSesiones = cantidadSesiones;
	}

	public boolean isRequiereDerivacion() {
		return requiereDerivacion;
	}

	public void setRequiereDerivacion(boolean requiereDerivacion) {
		this.requiereDerivacion = requiereDerivacion;
	}

	public int getCantidadSesiones() {
		return cantidadSesiones;
	}

	public void setCantidadSesiones(int cantidadSesiones) {
		this.cantidadSesiones = cantidadSesiones;
	}
}
