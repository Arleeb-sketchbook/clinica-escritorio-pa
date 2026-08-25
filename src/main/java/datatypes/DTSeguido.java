package datatypes;

import java.time.LocalDateTime;

public class DTSeguido {
	private final Long id;
	private final DTPrestacion prestacion;
	private final LocalDateTime fecha;

	public DTSeguido(Long id, DTPrestacion prestacion, LocalDateTime fecha) {
		this.id = id;
		this.prestacion = prestacion;
		this.fecha = fecha;
	}

	public Long getId() { return id; }
	public DTPrestacion getPrestacion() { return prestacion; }
	public LocalDateTime getFecha() { return fecha; }
}
