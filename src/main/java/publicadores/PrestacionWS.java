package publicadores;

public class PrestacionWS {
    private Long id;
    private String nombre;
    private double precio;
    private logica.Franja franja;
    private TipoPrestacion tipo;
    private Integer duracionMinutos;
    private Integer cantidadSesiones;
    private boolean requiereDerivacion;

    public PrestacionWS() {}
    public Long getId() { return id; }
    public void setId(Long valor) { this.id = valor; }
    public String getNombre() { return nombre; }
    public void setNombre(String valor) { this.nombre = valor; }
    public double getPrecio() { return precio; }
    public void setPrecio(double valor) { this.precio = valor; }
    public logica.Franja getFranja() { return franja; }
    public void setFranja(logica.Franja valor) { this.franja = valor; }
    public TipoPrestacion getTipo() { return tipo; }
    public void setTipo(TipoPrestacion valor) { this.tipo = valor; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer valor) { this.duracionMinutos = valor; }
    public Integer getCantidadSesiones() { return cantidadSesiones; }
    public void setCantidadSesiones(Integer valor) { this.cantidadSesiones = valor; }
    public boolean isRequiereDerivacion() { return requiereDerivacion; }
    public void setRequiereDerivacion(boolean valor) { this.requiereDerivacion = valor; }
}
