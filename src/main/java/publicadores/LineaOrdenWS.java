package publicadores;

public class LineaOrdenWS {
    private String nombrePrestacion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public LineaOrdenWS() {}
    public String getNombrePrestacion() { return nombrePrestacion; }
    public void setNombrePrestacion(String valor) { this.nombrePrestacion = valor; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int valor) { this.cantidad = valor; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double valor) { this.precioUnitario = valor; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double valor) { this.subtotal = valor; }
}
