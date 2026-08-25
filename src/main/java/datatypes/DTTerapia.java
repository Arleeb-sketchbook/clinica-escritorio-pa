package datatypes;

import logica.Franja;

public class DTTerapia extends DTPrestacion {
	private final boolean requiereDerivacion;
	private final int cantidadSesiones;

	public DTTerapia(Long id, String nombre, double precio, Franja franja,
			boolean requiereDerivacion, int cantidadSesiones) {
		super(id, nombre, precio, franja);
		this.requiereDerivacion = requiereDerivacion;
		this.cantidadSesiones = cantidadSesiones;
	}

	public boolean isRequiereDerivacion() { return requiereDerivacion; }
	public int getCantidadSesiones() { return cantidadSesiones; }
}
