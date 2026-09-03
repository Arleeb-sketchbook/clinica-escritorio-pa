package datatypes;

public class DTLineaOrden {
    private final Long id;
    private final DTPrestacion prestacion;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public DTLineaOrden(Long id, DTPrestacion prestacion, int cantidad, double precioUnitario, double subtotal) {
        this.id = id;
        this.prestacion = prestacion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Long getId() { return id; }
    public DTPrestacion getPrestacion() { return prestacion; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}
