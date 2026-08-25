package datatypes;

import logica.Franja;

public class DTPrestacion {
	private final Long id;
	private final String nombre;
	private final double precio;
	private final Franja franja;

	public DTPrestacion(Long id, String nombre, double precio, Franja franja) {
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
		this.franja = franja;
	}

	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public double getPrecio() { return precio; }
	public Franja getFranja() { return franja; }
}
