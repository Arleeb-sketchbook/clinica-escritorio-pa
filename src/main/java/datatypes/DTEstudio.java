package datatypes;

import logica.Franja;

public class DTEstudio extends DTPrestacion {
	private final int duracionMinutos;

	public DTEstudio(Long id, String nombre, double precio, Franja franja, int duracionMinutos) {
		super(id, nombre, precio, franja);
		this.duracionMinutos = duracionMinutos;
	}

	public int getDuracionMinutos() { return duracionMinutos; }
}
