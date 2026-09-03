package datatypes;

import java.time.LocalDateTime;
import java.util.List;

public class DTOrdenMedica {
    private final Long id;
    private final String emailPaciente;
    private final LocalDateTime fecha;
    private final List<DTLineaOrden> lineas;

    public DTOrdenMedica(Long id, String emailPaciente, LocalDateTime fecha, List<DTLineaOrden> lineas) {
        this.id = id;
        this.emailPaciente = emailPaciente;
        this.fecha = fecha;
        this.lineas = lineas;
    }

    public Long getId() { return id; }
    public String getEmailPaciente() { return emailPaciente; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DTLineaOrden> getLineas() { return lineas; }
}
