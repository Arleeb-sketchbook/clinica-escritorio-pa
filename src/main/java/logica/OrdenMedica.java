package logica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class OrdenMedica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "paciente_email", nullable = false)
	private Paciente paciente;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<LineaOrden> lineas = new ArrayList<>();

	protected OrdenMedica() {
	}

	public OrdenMedica(Paciente paciente) {
		this.paciente = paciente;
		this.fecha = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public List<LineaOrden> getLineas() {
		return Collections.unmodifiableList(lineas);
	}

	public void agregarLinea(Prestacion prestacion, int cantidad) {
		lineas.add(new LineaOrden(this, prestacion, cantidad));
	}

	public double getTotal() {
		return lineas.stream().mapToDouble(LineaOrden::getSubtotal).sum();
	}
}
