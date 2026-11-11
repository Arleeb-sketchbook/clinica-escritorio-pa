package publicadores;

public class ItemSolicitudWS {
    private Long idPrestacion;
    private int cantidad;

    public ItemSolicitudWS() {}
    public Long getIdPrestacion() { return idPrestacion; }
    public void setIdPrestacion(Long valor) { this.idPrestacion = valor; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int valor) { this.cantidad = valor; }
}
