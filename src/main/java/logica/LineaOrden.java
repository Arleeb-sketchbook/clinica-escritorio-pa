package logica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LineaOrden {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "orden_id", nullable = false)
	private OrdenMedica orden;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prestacion_id", nullable = false)
	private Prestacion prestacion;

	@Column(nullable = false)
	private int cantidad;

	@Column(nullable = false)
	private double precioUnitario;

	protected LineaOrden() {
	}

	public LineaOrden(OrdenMedica orden, Prestacion prestacion, int cantidad) {
		this.orden = orden;
		this.prestacion = prestacion;
		this.cantidad = cantidad;
		this.precioUnitario = prestacion.getPrecio();
	}

	public Long getId() {
		return id;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public OrdenMedica getOrden() {
		return orden;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public double getSubtotal() {
		return cantidad * precioUnitario;
	}
}
