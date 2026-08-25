package logica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Prestacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String nombre;

	@Column(nullable = false)
	private double precio;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Franja franja;

	protected Prestacion() {
	}

	protected Prestacion(String nombre, double precio, Franja franja) {
		this.nombre = nombre;
		this.precio = precio;
		this.franja = franja;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Franja getFranja() {
		return franja;
	}

	public void setFranja(Franja franja) {
		this.franja = franja;
	}
}
