package publicadores;

public class OrdenMedicaWS {
    private Long id;
    private String fecha;
    private java.util.List<LineaOrdenWS> lineas = new java.util.ArrayList<>();
    private double total;

    public OrdenMedicaWS() {}
    public Long getId() { return id; }
    public void setId(Long valor) { this.id = valor; }
    public String getFecha() { return fecha; }
    public void setFecha(String valor) { this.fecha = valor; }
    public java.util.List<LineaOrdenWS> getLineas() { return lineas; }
    public void setLineas(java.util.List<LineaOrdenWS> valor) { this.lineas = valor; }
    public double getTotal() { return total; }
    public void setTotal(double valor) { this.total = valor; }
}
