package publicadores;

public class SeguidoWS {
    private PrestacionWS prestacion;
    private String fecha;

    public SeguidoWS() {}
    public PrestacionWS getPrestacion() { return prestacion; }
    public void setPrestacion(PrestacionWS valor) { this.prestacion = valor; }
    public String getFecha() { return fecha; }
    public void setFecha(String valor) { this.fecha = valor; }
}
