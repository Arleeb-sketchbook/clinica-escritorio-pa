package logica;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "paciente_email", "prestacion_id" }))
public class Seguido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "paciente_email", nullable = false)
	private Paciente paciente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prestacion_id", nullable = false)
	private Prestacion prestacion;

	@Column(nullable = false)
	private LocalDateTime fecha;

	protected Seguido() {
	}

	public Seguido(Paciente paciente, Prestacion prestacion) {
		this.paciente = paciente;
		this.prestacion = prestacion;
		this.fecha = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}
}
